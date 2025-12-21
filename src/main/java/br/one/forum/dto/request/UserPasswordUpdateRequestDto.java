package br.one.forum.dto.request;

import br.one.forum.infra.validation.StrongPassword;
import jakarta.validation.constraints.NotBlank;

public record UserPasswordUpdateRequestDto(

        @StrongPassword
        @NotBlank
        String password,

        String passwordMatch

) {
}
