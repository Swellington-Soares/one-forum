package br.one.forum.dtos;


import jakarta.validation.constraints.NotBlank;

public record UserProfileUpdateRequestDto(
        @NotBlank
        String photo
) {
}
