package br.one.forum.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AvatarImageService {

    @Value("${api.base-url}")
    private String baseUrl;

    public String getAvatarImageUrl(long seed) {
        return baseUrl + "/avatars/" + (seed % 46) + ".png";
    }

}
