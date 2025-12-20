package br.one.forum.dtos.request;

import br.one.forum.validation.interfaces.PasswordMatch;
import br.one.forum.validation.interfaces.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@PasswordMatch
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
