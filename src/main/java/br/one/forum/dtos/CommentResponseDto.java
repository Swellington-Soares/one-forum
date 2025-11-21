package br.one.forum.dtos;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link br.one.forum.entities.Comment}
 */
public record CommentResponseDto(Integer id, Integer topicId, Integer userId, String userProfileName,
                                 String userProfilePhoto,
                                 @NotBlank String content, Instant createdAt,
                                 Instant updateAt) implements Serializable {
}