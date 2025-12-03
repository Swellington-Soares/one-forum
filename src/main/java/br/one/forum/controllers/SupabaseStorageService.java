package br.one.forum.controllers;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
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
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        RequestBody fileBody = RequestBody.create(
                file.getBytes(),
                MediaType.parse(Objects.requireNonNull(file.getContentType()))
        );

        Request request = new Request.Builder()
                .url(supabaseUrl + "/storage/v1/object/" + bucket + "/" + fileName)
                .addHeader("Authorization", "Bearer " + supabaseKey)
                .addHeader("Content-Type", file.getContentType())
                .put(fileBody)
                .build();

        Response response = client.newCall(request).execute();

      if (!response.isSuccessful()) {
            throw new IOException("Falha ao realizar upload: " + response.message());
        }

        return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + fileName;
    }
}