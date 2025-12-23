package br.one.forum.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AvatarImageService {

    @Value("${api.base-url}")
    private String baseApiUrl;

    public String getAvatarImageUrl(long seed) {
        return baseApiUrl + "/avatars/" + (seed % 46) + ".png";
    }

}
