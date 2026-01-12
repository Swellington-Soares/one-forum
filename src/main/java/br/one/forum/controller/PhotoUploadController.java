package br.one.forum.controller;


import br.one.forum.infra.security.AppUserDetailsInfo;
import br.one.forum.service.ProfileImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/upload")
@RequiredArgsConstructor
public class PhotoUploadController {
    
    private final ProfileImageService processImageService;

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<?> requestPhotoUpload(@RequestParam("file") MultipartFile file) {
        var user = ((AppUserDetailsInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).user();
        processImageService.processImage(file, user);
        return ResponseEntity.accepted().build();
    }
}
