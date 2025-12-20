package br.one.forum.controllers;

import java.io.IOException;

import br.one.forum.dtos.response.UploadImageToStorageResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {

    private final SupabaseStorageService storage;

    public UploadController(SupabaseStorageService storage) {
        this.storage = storage;
    }

    @PostMapping
    public ResponseEntity<UploadImageToStorageResponseDto> upload(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(new UploadImageToStorageResponseDto(storage.upload(file)));
    }
}
