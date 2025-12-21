package br.one.forum.dto.request;

import jakarta.validation.constraints.Email;

public record ConfirmAccountRequestDto(
        @Email
        String email
) {
}
