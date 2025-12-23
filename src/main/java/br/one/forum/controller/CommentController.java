package br.one.forum.controller;

import br.one.forum.dto.request.CommentCreateRequestDto;
import br.one.forum.dto.request.UpdateCommentRequestDto;
import br.one.forum.dto.response.CommentResponseDto;
import br.one.forum.entity.CurrentUser;
import br.one.forum.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/topics/{topicId}/comments")
@RequiredArgsConstructor
class CommentController {

    private final CommentService commentService;
    private final CurrentUser auth;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long topicId,
            @RequestBody @Valid CommentCreateRequestDto dto) {
        var comment = commentService.createComment(topicId, auth.getUser().getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping
    public Page<CommentResponseDto> getComments(@PathVariable Long topicId,
                                                Pageable pageable) {
        return commentService.findAllByTopicId(topicId, pageable);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDto> getOneComment(
            @PathVariable Long topicId,
            @PathVariable("id") Long commentId) {
        return ResponseEntity.ok(commentService.findByTopicIdAndCommentId(topicId, commentId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long topicId,
            @PathVariable("id") Long commentId,
            @RequestBody @Valid UpdateCommentRequestDto dto) {
        return ResponseEntity.ok(commentService.updateComment(
               commentId,
               topicId,
               auth.getUser().getId(),
               dto
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long topicId,
            @PathVariable("id") Long commentId ) {
        commentService.deleteComment(
                topicId,
                commentId,
                auth.getUser().getId()
        );
        return ResponseEntity.noContent().build();
    }

}
