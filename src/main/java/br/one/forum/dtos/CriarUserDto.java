package br.one.forum.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarUserDto(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 4) String password,
        @NotBlank String name
) {

}
