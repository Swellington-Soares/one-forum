package br.one.forum.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

/**
 * DTO for {@link br.one.forum.entities.Topic}
 */
public record TopicRegisterDto( @NotNull String title, @NotNull String content,
                               Set<TopicCategoryDto> categories) implements Serializable {


    /**
     * DTO for {@link br.one.forum.entities.Category}
     */
    public record TopicCategoryDto(Integer id,@NotNull @Size(max = 50) String name) implements Serializable {
    }
}