package br.one.forum.services;

import br.one.forum.configuration.UploadProperties;
import br.one.forum.exception.FileUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final UploadProperties uploadProperties;

    public String saveProfileImage(MultipartFile file) {
        validateFile(file);
        String baseDirectory = uploadProperties.getBaseDir();
        String imageDirectory = uploadProperties.getImages().getDir();
        try {
            File dir = new File(Path.of(baseDirectory, imageDirectory).toString());
            if (!dir.exists())
                if (!dir.mkdirs()) throw new RuntimeException("Directory could not be created");
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(baseDirectory, imageDirectory, fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (Exception e) {
            throw new FileUploadException();
        }
    }

    private void validateFile(MultipartFile file) {

        if (file.isEmpty()) throw new IllegalArgumentException("Arquivo vazio.");

        var imgProps = uploadProperties.getImages();
        String[] allowed = imgProps.getAllowedTypes().split(",");
        if (Arrays.stream(allowed).noneMatch(
                type -> type.equalsIgnoreCase(file.getContentType()))) {
            throw new IllegalArgumentException("Tipo de arquivo não permitido: " + file.getContentType());
        }
        long maxSize = DataSize.parse(imgProps.getMaxSize()).toBytes();
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("Arquivo excede o tamanho máximo de " + imgProps.getMaxSize());
        }
    }

}
