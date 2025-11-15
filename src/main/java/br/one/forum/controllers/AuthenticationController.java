package br.one.forum.controllers;

import br.one.forum.dtos.AuthenticationRequestDto;
import br.one.forum.dtos.LoginResponseDTO;
import br.one.forum.dtos.UserRegisterRequestDto;
import br.one.forum.entities.User;
import br.one.forum.exception.UserAlreadyRegisteredException;
import br.one.forum.exception.UserNotFoundException;
import br.one.forum.exception.UserPasswordNotMatchException;
import br.one.forum.services.AuthenticationService;
import br.one.forum.services.TokenService;
import br.one.forum.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationRequestDto data) {                   
        return ResponseEntity.ok(new LoginResponseDTO(authenticationService.login(data), "Authenticated user."));    
    }
    //TODO: MOVER PARA UserController
    // @PostMapping("/register")
    // public ResponseEntity register(@RequestBody @Valid UserRegisterRequestDto data) {
    //     try {
    //         boolean success = authenticationService.register(data);
    //         if (success) {
    //             return ResponseEntity.status(HttpStatus.CREATED).build();
    //         }
    //         return ResponseEntity.badRequest().build();
    //     } catch (UserAlreadyRegisteredException e) {
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already registered with this email.");
    //     }
    // }
}
