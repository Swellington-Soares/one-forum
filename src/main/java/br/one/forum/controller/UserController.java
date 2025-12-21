package br.one.forum.controller;


import br.one.forum.dto.request.UserRegisterRequestDto;
import br.one.forum.dto.response.CommentResponseDto;
import br.one.forum.dto.response.UserProfileResponseDto;
import br.one.forum.entity.CurrentUser;
import br.one.forum.mapper.UserMapper;
import br.one.forum.service.CommentService;
import br.one.forum.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final CommentService commentService;
    private final CurrentUser auth;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<UserProfileResponseDto> getUserAuthProfile() {
        return ResponseEntity.ok(userMapper.toUserProfileInfoResponseDto(auth.getUser()));
    }

    @GetMapping("/{id}")
    ResponseEntity<UserProfileResponseDto> getUserById(@PathVariable Long id) {

        if (auth.getUser() != null && auth.getUser().getId().equals(id)) {
            return ResponseEntity
                    .status(HttpStatus.TEMPORARY_REDIRECT)
                    .header(HttpHeaders.LOCATION, "/users/profile")
                    .build();
        }

        var user = userService.findUserById(id);
        return ResponseEntity.ok(userMapper.toUserProfileInfoResponseDto(user));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UserRegisterRequestDto data) {
        userService.registerUser(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    //@PatchMapping("/{id}")
//    @PreAuthorize("isAuthenticated() && @auth.isOwner(#id) ")
//    public ResponseEntity<Void> updateProfile(
//            @PathVariable int id,
//            @RequestBody(required = false) @Valid UserProfileUpdateRequestDto data) {
//        userService.updateUserProfile(id, data);
//        return ResponseEntity.noContent().build();
//    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<Slice<CommentResponseDto>> getAllUserComments(
            @PathVariable Long id,
            Pageable pageable) {
        return ResponseEntity.ok(commentService.findAllByAuthorId(id, pageable));
    }
}
