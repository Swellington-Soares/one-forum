package br.one.forum.dtos.request;

import br.one.forum.validation.StrongPassword;
import jakarta.validation.constraints.NotBlank;

public record UserPasswordChangeRequestDto(

        @NotBlank
        String token,

        @NotBlank
        String newPassword,

        @NotBlank
        @StrongPassword
        String confirmPassword
) {
}
