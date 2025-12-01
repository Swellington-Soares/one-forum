package br.one.forum.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

/**
 * DTO for {@link br.one.forum.entities.Topic}
 */
public record TopicResponseDto(int id,
                               String title,
                               int likes,
                               int commentCount,
                               String content,
                               boolean likedByCurrentUser,
                               TopicResponseDto.UserTopicDto author,
                               Instant createdAt,
                               Instant updatedAt,
                               Set<TopicCategoryDto> categories) implements Serializable {
    public record UserTopicDto(int id,
                               Instant createdAt,
                               UserTopicProfileDto profile) implements Serializable {
        public record UserTopicProfileDto(String name, String photo) implements Serializable {
        }
    }

    public record TopicCategoryDto(String name) implements Serializable {
    }
}