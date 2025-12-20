package br.one.forum.dtos.request;

import br.one.forum.validation.interfaces.StrongPassword;
import jakarta.validation.constraints.NotBlank;

public record UserPasswordUpdateRequestDto(
        @NotBlank
        String password,

        @NotBlank
        @StrongPassword
        String newPassword
) {
}
