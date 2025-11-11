package br.one.forum.controller;

import br.one.forum.dtos.CommentResponseDto;
import br.one.forum.dtos.CriarCommentDto;
import br.one.forum.dtos.UpdateCommentDto;
import br.one.forum.entities.Comment;
import br.one.forum.repositories.CommentRepository;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.repositories.UserRepository;
import br.one.forum.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<CommentResponseDto> saveComment(@RequestBody @Valid CriarCommentDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarComentario(dto));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDto> getOneComment(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateCommentDto dto) {

        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
