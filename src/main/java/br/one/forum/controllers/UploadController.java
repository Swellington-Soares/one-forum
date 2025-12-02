package br.one.forum.controllers;

import java.io.IOException;

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
    public String upload(@RequestParam("file") MultipartFile file) throws IOException {
        return storage.upload(file);
    }
}
