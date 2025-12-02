package br.one.forum.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link br.one.forum.entities.Comment}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserCommentResponseDto(Integer id,
                                     Integer topicId,
                                     String topicTitle,
                                     Integer authorId,
                                     String authorProfileName,
                                     String authorProfilePhoto,
                                     String content,
                                     Instant createdAt, Instant updateAt) implements Serializable {
}