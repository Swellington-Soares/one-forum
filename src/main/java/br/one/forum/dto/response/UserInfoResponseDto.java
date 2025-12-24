package br.one.forum.dto.response;

import java.time.Instant;

public record UserInfoResponseDto(
        Long id,
        Instant createdAt,
        String profileName,
        String profilePhoto)  {
}