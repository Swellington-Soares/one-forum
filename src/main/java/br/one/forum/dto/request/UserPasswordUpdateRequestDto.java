package br.one.forum.dto.request;

import br.one.forum.infra.validation.PasswordMatch;
import br.one.forum.infra.validation.StrongPassword;
import jakarta.validation.constraints.NotBlank;

@PasswordMatch(passwordField = "password", confirmPasswordField = "matchPassword")
public record UserPasswordUpdateRequestDto(

        @StrongPassword
        @NotBlank
        String password,

        String matchPassword

) {
}
