package br.one.forum.dtos;

import jakarta.validation.constraints.Email;

public record ConfirmAccountRequestDto(
        @Email
        String email
) {
}
