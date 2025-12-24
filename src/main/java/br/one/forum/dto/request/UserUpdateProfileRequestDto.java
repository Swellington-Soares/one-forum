package br.one.forum.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateProfileRequestDto(
        @NotBlank
        @Size(max = 60)
        String name,

        @Size(max = 1500)
        String bio
) {
}
