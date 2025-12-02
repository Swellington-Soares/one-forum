package br.one.forum.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record TopicEditRequestDto(
        @Size(max = 60)
        @NotBlank String title,
        @Size(max = 1500) @NotBlank String content) implements Serializable {
}