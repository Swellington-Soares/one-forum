package br.one.forum.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

/**
 * DTO for {@link br.one.forum.entities.Topic}
 */
public record TopicResponseDto(Integer id,
                               @NotNull String title,
                               @NotNull String content,
                               Integer likes,
                               Boolean likedByCurrentUser,
                               @NotNull TopicResponseDto.UserTopicDto user, Instant createdAt, Instant updatedAt,
                               Set<TopicCategoryDto> categories) implements Serializable {
    public record UserTopicDto(Integer id, @Size(max = 255) String email, Instant createdAt,
                               UserTopicProfileDto profile) implements Serializable {
        public record UserTopicProfileDto(Integer id, @NotNull String name, String photo) implements Serializable {
        }
    }
    public record TopicCategoryDto(@NotNull @Size(max = 50) String name) implements Serializable {
    }
}