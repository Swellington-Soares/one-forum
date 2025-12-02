package br.one.forum.controllers;

import br.one.forum.dtos.request.AuthenticationRequestDto;
import br.one.forum.dtos.response.LoginResponseDto;
import br.one.forum.dtos.request.RefreshTokenRequestDto;
import br.one.forum.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid AuthenticationRequestDto data) {
        return ResponseEntity.ok(authenticationService.login(data));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@RequestBody @Valid RefreshTokenRequestDto data) {
        return ResponseEntity.ok(authenticationService.refreshToken(data.refreshToken()));
    }

}
