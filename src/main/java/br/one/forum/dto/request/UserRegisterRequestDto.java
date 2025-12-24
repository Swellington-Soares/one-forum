package br.one.forum.dto.request;

import br.one.forum.infra.validation.PasswordMatch;
import br.one.forum.infra.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@PasswordMatch(passwordField = "password", confirmPasswordField = "matchPassword")
public record UserRegisterRequestDto(
        @NotBlank
        @Email
        String email,

        @NotBlank
        @StrongPassword
        @Size(min = 6, max = 16)
        String password,

        @NotBlank
        String matchPassword,

        @NotBlank
        @Size(max = 75)
        String name
) {
}
