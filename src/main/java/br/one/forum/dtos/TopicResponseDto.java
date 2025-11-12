package br.one.forum.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

/**
 * DTO for {@link br.one.forum.entities.Topic}
 */
public record TopicResponseDto(Integer id, @NotNull String title, @NotNull String content,
                               Integer likes,
                               Boolean likedByCurrentUser,
                               @NotNull TopicResponseDto.UserTopicDto user, Instant createdAt, Instant updatedAt,
                               Set<TopicCategoryDto> categories) implements Serializable {
    /**
     * DTO for {@link br.one.forum.entities.User}
     */
    public record UserTopicDto(Integer id, @Size(max = 255) String email, Instant createdAt,
                               UserTopicProfileDto profile) implements Serializable {
        /**
         * DTO for {@link br.one.forum.entities.Profile}
         */
        public record UserTopicProfileDto(Integer id, @NotNull String name, String photo) implements Serializable {
        }
    }

    /**
     * DTO for {@link br.one.forum.entities.Category}
     */
    public record TopicCategoryDto(Integer id,@NotNull @Size(max = 50) String name) implements Serializable {
    }
}