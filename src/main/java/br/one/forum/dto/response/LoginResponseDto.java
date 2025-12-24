package br.one.forum.dto.response;

import br.one.forum.dto.JwtTokenDto;

public record LoginResponseDto(
        JwtTokenDto accessToken,
        JwtTokenDto refreshToken
) {
}
