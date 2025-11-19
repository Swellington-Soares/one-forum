package br.one.forum.dtos;

import br.one.forum.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record UserRegisterRequestDto(

        @NotBlank
        @Email
        String email,

        @NotBlank
        @StrongPassword
        String password,

        @NotBlank
        String matchPassword,

        @NotBlank
        @Size(max = 75)
        String name,

        @NotBlank
        @URL
        String avatarUrl
) {
}
