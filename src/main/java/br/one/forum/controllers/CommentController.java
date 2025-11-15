package br.one.forum.controllers;

import br.one.forum.dtos.CommentResponseDto;
import br.one.forum.dtos.CreateCommentDto;
import br.one.forum.dtos.UpdateCommentDto;
import br.one.forum.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CommentResponseDto> saveComment(@RequestBody @Valid CreateCommentDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createComment(dto));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> listAllComment() {
        return ResponseEntity.ok(service.listAllComment());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDto> getOneComment(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findByIdComment(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateCommentDto dto) {

        return ResponseEntity.ok(service.updateComment(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer id) {
        service.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
