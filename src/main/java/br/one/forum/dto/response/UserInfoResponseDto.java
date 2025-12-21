package br.one.forum.dto.response;

import br.one.forum.entity.User;

import java.io.Serializable;
import java.time.Instant;

public record UserInfoResponseDto(
        Long id,
        Instant createdAt,
        String profileName,
        String profilePhoto)  {
}