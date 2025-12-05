package br.one.forum.dtos;

import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;

/**
 * DTO for {@link br.one.forum.entities.User}
 */
public record UserProfileUpdateRequestDto(
        @Size(min = 4, max = 50)
        String name,

        @URL
        String photo) implements Serializable {

}