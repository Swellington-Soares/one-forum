package br.one.forum.controllers;


import br.one.forum.dtos.UserCommentResponseDto;
import br.one.forum.dtos.UserProfileUpdateRequestDto;
import br.one.forum.dtos.UserRegisterRequestDto;
import br.one.forum.mappers.UserMapper;
import br.one.forum.services.CommentService;
import br.one.forum.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final CommentService commentService;


    @GetMapping("/{id}")
    ResponseEntity<Object> getUserById(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findUserById(id, false);
        if (userDetails != null && userDetails.getUsername().equals(user.getEmail())) {
            return ResponseEntity.ok(userMapper.toAuthUserDto(user));
        }
        return ResponseEntity.ok(userMapper.toPublicDto(user));
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
            @PathVariable int id,
            @RequestBody(required = false) @Valid UserProfileUpdateRequestDto data) {
        userService.updateUserProfile(id, data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @GetMapping("/{id}/comments")
    public Page<UserCommentResponseDto> getAllUserComments(
            @PathVariable int id,
            Pageable pageable) {
        return commentService.findAllByAuthorId(id, pageable);
    }
}

