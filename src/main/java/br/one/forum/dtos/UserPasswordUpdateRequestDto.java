package br.one.forum.dtos;

import br.one.forum.validation.StrongPassword;
import jakarta.validation.constraints.NotBlank;

public record UserPasswordUpdateRequestDto(
        @NotBlank
        String password,

        @NotBlank
        @StrongPassword
        String newPassword
) {
}
