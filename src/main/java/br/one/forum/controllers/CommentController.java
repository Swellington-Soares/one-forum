package br.one.forum.controllers;

import br.one.forum.dtos.request.CommentCreateRequestDto;
import br.one.forum.dtos.request.UpdateCommentRequestDto;
import br.one.forum.dtos.response.CommentResponseDto;
import br.one.forum.entities.CurrentUser;
import br.one.forum.services.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
public class CommentController {

    private final CommentService commentService;
    private final CurrentUser auth;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable int topicId,
            @RequestBody @Valid CommentCreateRequestDto dto) {
        var comment = commentService.createComment(topicId, auth.getUser(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @Operation(
            summary = "Lista de comentários do tópico",
            description = "Retorna uma lista paginada de comentários do tópico, com opção de filtrar por autor e ordenar por curtidas."
    )
    @Parameter(
            name = "sort",
            description = "Ordenação no formato: campo,asc ou campo,desc",
            example = "createdAt,desc"
    )
    @Parameter(
            name = "page",
            description = "Número da página (0..N)",
            example = "0"
    )
    @Parameter(
            name = "size",
            description = "Quantidade de itens por página",
            example = "10"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de comentários do tópico retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommentResponseDto.class))
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno inesperado")
    })
    @GetMapping
    public Page<CommentResponseDto> getComments(@PathVariable int topicId,
                                                Pageable pageable) {
        return commentService.findAllByTopicId(topicId, pageable);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDto> getOneComment(
            @PathVariable int topicId,
            @PathVariable int id) {
        return ResponseEntity.ok(commentService.findByTopicIdAndCommentId(topicId, id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable int topicId,
            @PathVariable int id,
            @RequestBody @Valid UpdateCommentRequestDto dto) {
        return ResponseEntity.ok(commentService.updateComment(auth.getUser().getId(), topicId, id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteComment(
            @PathVariable int topicId,
            @PathVariable int id) {
        commentService.deleteComment(auth.getUser().getId(), topicId, id);
        return ResponseEntity.noContent().build();
    }
}
