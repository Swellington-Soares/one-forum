package br.one.forum.controller;


import br.one.forum.dto.request.UserRegisterRequestDto;
import br.one.forum.dto.request.UserUpdateProfileRequestDto;
import br.one.forum.dto.response.CommentResponseDto;
import br.one.forum.dto.response.TopicResponseDto;
import br.one.forum.dto.response.UserProfileResponseDto;
import br.one.forum.entity.CurrentUser;
import br.one.forum.mapper.TopicMapper;
import br.one.forum.mapper.UserMapper;
import br.one.forum.service.CommentService;
import br.one.forum.service.TopicService;
import br.one.forum.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TopicMapper topicMapper;
    private final TopicService topicService;
    private final CommentService commentService;
    private final CurrentUser auth;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<UserProfileResponseDto> getUserAuthProfile() {
        return ResponseEntity.ok(userService.retrieveUserProfile(auth.getUser().getId()));
    }

    @GetMapping("/{id}")
    ResponseEntity<UserProfileResponseDto> getUserById(@PathVariable Long id) {

        if (auth.getUser() != null && auth.getUser().getId().equals(id)) {
            return ResponseEntity
                    .status(HttpStatus.TEMPORARY_REDIRECT)
                    .header(HttpHeaders.LOCATION, "/users/profile")
                    .build();
        }
        return ResponseEntity.ok(userService.retrieveUserProfile(id));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UserRegisterRequestDto data) {
        userService.registerUser(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/update-profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateProfile(
            @RequestBody @Valid UserUpdateProfileRequestDto dto) {
        userService.updateUserProfile(auth.getUser(), dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<Slice<CommentResponseDto>> getAllUserComments(
            @PathVariable Long id,
            Pageable pageable) {
        return ResponseEntity.ok(commentService.findAllByAuthorId(id, pageable));
    }


    @GetMapping("/{id}/topics")
    public ResponseEntity<Slice<TopicResponseDto>> getAllUserTopics(
            @PathVariable Long id,
            Pageable pageable) {
        return ResponseEntity.ok(
                topicService.getAll(id, false, null, null, pageable)
                        .map( t -> topicMapper.toResumedResponseDto(t, null)));
    }
}
