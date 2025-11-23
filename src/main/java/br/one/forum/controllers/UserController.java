package br.one.forum.controllers;


import br.one.forum.dtos.UserProfileUpdateRequestDto;
import br.one.forum.dtos.UserRegisterRequestDto;
import br.one.forum.entities.CurrentUser;
import br.one.forum.entities.User;
import br.one.forum.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //private final CurrentUser auth;

    @GetMapping("/{id}")
    User getUserById(@PathVariable int id) {
        return userService.findUserById(id, false);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UserRegisterRequestDto data) {
        userService.registerUser(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

   @PatchMapping("/{id}")
   @ResponseStatus(HttpStatus.NO_CONTENT)
   @PreAuthorize("isAuthenticated() && @auth.isOwner(#id) ")
    public ResponseEntity<Void> updateProfile(
            @PathVariable Integer id,
            @RequestBody(required = false) @Valid UserProfileUpdateRequestDto data) {
        userService.updateUserProfile(id, data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

