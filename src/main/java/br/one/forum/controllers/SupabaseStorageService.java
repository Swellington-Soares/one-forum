package br.one.forum.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String upload(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg";
        // supabase follows S3 naming convention, the invalid characters must be handled.
        String sanitizedName = originalName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        String fileName = UUID.randomUUID() + "_" + sanitizedName;

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
                String errorResponse = response.body() != null ? response.body().string() : "No error message";

                // Default error message
                String cleanMessage = "Error while processing image in storage.";

                // Tries to read the error message from the supabase, if exists.
                try {
                    JsonNode root = objectMapper.readTree(errorResponse);
                    if (root.has("message")) {
                        cleanMessage = root.get("message").asText();
                    }
                } catch (Exception e) {
                    cleanMessage = errorResponse;
                }

                throw new IOException(cleanMessage);
            }

            return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + fileName;
        } catch (IOException e) {
            System.err.println("Upload failed: " + e.getMessage());
            throw new IOException(e.getMessage());
        }
    }
}