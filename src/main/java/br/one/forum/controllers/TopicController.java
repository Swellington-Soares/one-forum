package br.one.forum.controllers;


import br.one.forum.dtos.TopicCreateRequestDto;
import br.one.forum.dtos.TopicEditRequestDto;
import br.one.forum.dtos.TopicResponseDto;
import br.one.forum.entities.User;
import br.one.forum.mappers.TopicResponseMapper;
import br.one.forum.services.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;
    private final TopicResponseMapper topicResponseMapper;

    private final User currentLoggedUser;

    @GetMapping
    public Page<TopicResponseDto> getAllTopics(
            @RequestParam(required = false) Long authorId,
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
