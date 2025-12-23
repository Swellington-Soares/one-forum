package br.one.forum.service;

import br.one.forum.configuration.UploadImageProperties;
import br.one.forum.entity.User;
import br.one.forum.exception.api.UserCannotUpdatePhotoOrInvalid;
import br.one.forum.infra.worker.processimage.ProcessImageQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileImageService {

    private final UploadImageProperties imageProperties;
    private final ProcessImageQueue processImageQueue;
    private final UserService userService;

    private void _processImage(InputStream photoFile, Long userId) {
        try {
            String uploadBase = imageProperties.getStoragePathBase();

            if (uploadBase == null || uploadBase.isBlank()) {
                uploadBase = "upload/";
            }

            var imageName = "profile_" + userId.toString() + ".png";
            Path baseDir = Paths.get(uploadBase);

            if (!baseDir.toFile().exists()) {
                if (!baseDir.toFile().mkdirs())
                    throw new RuntimeException("Unable to create directory: " + baseDir.toFile().getAbsolutePath());
            }
            Path filePath = baseDir.resolve(imageName);

            Thumbnails.of(photoFile)
                    .crop(Positions.CENTER)
                    .size(512, 512)
                    .keepAspectRatio(true)
                    .outputFormat("png")
                    .outputQuality(1.0)
                    .toFile(filePath.toFile());

            userService.updateUserProfileImage(userId, "/profile/" + imageName);

        } catch (Exception e) {
            log.error("_processImage: {}", e.getMessage());
        }

    }

    public void processImage(MultipartFile file, User user) {
        try {
            validate(file);
            validateUser(user);
            var inputStream = file.getInputStream();
            processImageQueue.AddJob(() -> _processImage(inputStream, user.getId()));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void validateUser(User user) {
        if (user == null || user.getId() == null)
            throw new UserCannotUpdatePhotoOrInvalid();
    }

    private boolean isImageTypeValid(MultipartFile file) {
        String type = file.getContentType();
        return type != null && imageProperties.getAllowedExtensions()
                .stream()
                .anyMatch(type::contains);
    }

    private void validate(MultipartFile file) {
        if (file.isEmpty() || !isImageTypeValid(file))
            throw new IllegalArgumentException("Invalid file");
    }
}
