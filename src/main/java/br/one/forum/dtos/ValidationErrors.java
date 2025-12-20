package br.one.forum.dtos;

import java.util.List;
import java.util.Map;

public record ValidationErrors(
        Map<String, List<String>> fields,
        List<String> global
) {
}
