package br.one.forum.controllers;

import br.one.forum.exception.api.StorageUploadResponseException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucket;

    private final OkHttpClient client = new OkHttpClient();

    public String upload(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + sanitizeFileName(file);

        String contentType = file.getContentType();
        if (contentType == null) contentType = "image/jpeg";

        RequestBody fileBody = RequestBody.create(
                file.getBytes(),
                MediaType.parse(contentType)
        );

        Request request = new Request.Builder()
                .url(supabaseUrl + "/storage/v1/object/" + bucket + "/" + fileName)
                .addHeader("Authorization", "Bearer " + supabaseKey)
                .addHeader("Content-Type", contentType)
                .put(fileBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorResponse = response.body() != null ? response.body().string() : "Unidentified error during upload";
                throw new StorageUploadResponseException(errorResponse);
            }

            return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + fileName;
        } catch (IOException e) {
            throw new StorageUploadResponseException(e.getMessage());
        }
    }

    private String sanitizeFileName(MultipartFile file) {
        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file" + new Date().toString();
        return originalName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }
}