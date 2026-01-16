package br.one.forum.controller;

import br.one.forum.dto.request.ConfirmAccountRequestDto;
import br.one.forum.dto.request.LoginRequestDto;
import br.one.forum.dto.request.RefreshTokenRequestDto;
import br.one.forum.dto.response.LoginResponseDto;
import br.one.forum.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
class LoginController {

    private final LoginService loginService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody @Valid LoginRequestDto dto
            ) {
        return ResponseEntity.ok( loginService.login(dto) );
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@RequestBody @Valid RefreshTokenRequestDto data) {
        return ResponseEntity.ok(loginService.refreshToken(data.refreshToken()));
    }

    @PostMapping("/request-confirm-account")
    public ResponseEntity<Void> requestConfirmAccount(
            @RequestBody @Valid ConfirmAccountRequestDto dto
    ) {
        loginService.requestConfirmationAccountToken(dto.email());
        return ResponseEntity.ok().build();
    }


}
