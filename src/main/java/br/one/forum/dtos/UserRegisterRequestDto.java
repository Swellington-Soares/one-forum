package br.one.forum.dtos;

import br.one.forum.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterRequestDto(

        @NotBlank
        @Email
        String email,

        @NotBlank
        @StrongPassword()
        String password,

        @NotBlank
        String name
) {
}
