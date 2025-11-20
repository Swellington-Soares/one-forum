package br.one.forum.controllers;


import br.one.forum.dtos.TopicCreateRequestDto;
import br.one.forum.dtos.TopicEditRequestDto;
import br.one.forum.dtos.TopicResponseDto;
import br.one.forum.entities.User;
import br.one.forum.mappers.TopicResponseMapper;
import br.one.forum.services.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Tópicos", description = "Operações relacionadas a tópicos")
@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;
    private final TopicResponseMapper topicResponseMapper;
    private final User currentLoggedUser;

    @Operation(
            summary = "Lista de tópicos",
            description = "Retorna uma lista paginada de tópicos, com opção de filtrar por autor e ordenar por curtidas."
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
                    description = "Lista de tópicos retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TopicResponseDto.class))
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno inesperado")
    })
    @GetMapping
    public Page<TopicResponseDto> getAllTopics(
            @Parameter(
                    description = "ID do autor para filtrar a lista. Opcional.",
                    example = "12"
            )
            @RequestParam(required = false) Long authorId,

            @Parameter(
                    description = "Se true, ordena os tópicos pelos mais curtidos.",
                    example = "true"
            )
            @RequestParam(required = false) Boolean moreLiked,
            Pageable pageable
    ) {

        return topicService.getAll(authorId, moreLiked, pageable)
                .map(t -> topicResponseMapper.toDtoExcludeContent(t, currentLoggedUser));
    }

    @GetMapping("/{topicId}")
    public EntityModel<TopicResponseDto> getTopic(@PathVariable("topicId") int topicId) {
        return EntityModel.of(topicResponseMapper.toDto(topicService.findTopicById(topicId), currentLoggedUser));
    }

    @PostMapping
    public ResponseEntity<TopicResponseDto> createTopic(@RequestBody @Valid TopicCreateRequestDto data) {

        TopicResponseDto created = topicResponseMapper
                .toDtoExcludeContent(
                        topicService.createTopic(currentLoggedUser, data),
                        null
                );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);

    }

    @PutMapping("/{topicId}")
    public ResponseEntity<TopicResponseDto> createTopic(
            @PathVariable("topicId") int topicId,
            @RequestBody @Valid TopicEditRequestDto data) {
        var editedTopic = topicService.editTopic(topicId, data, currentLoggedUser);
        return ResponseEntity.ok(topicResponseMapper.toDtoExcludeContent(editedTopic, currentLoggedUser));
    }


    @DeleteMapping("/{topicId}")
    public ResponseEntity<Void> deleteTopic(@PathVariable("topicId") int topicId) {
        topicService.deleteTopic(topicId, currentLoggedUser);
        return ResponseEntity.notFound().build();
    }


}
