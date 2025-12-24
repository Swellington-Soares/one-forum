package br.one.forum.controller;

import br.one.forum.dto.request.TopicCreateRequestDto;
import br.one.forum.dto.request.TopicEditRequestDto;
import br.one.forum.dto.response.TopicLikeResponseDto;
import br.one.forum.dto.response.TopicResponseDto;
import br.one.forum.entity.CurrentUser;
import br.one.forum.mapper.TopicMapper;
import br.one.forum.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;
    private final TopicMapper topicMapper;
    private final CurrentUser auth;

    @GetMapping
    public ResponseEntity<Slice<TopicResponseDto>> getAllTopicsFiltered(
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean moreLiked,
            Pageable pageable) {
        var user = auth.getUser();
        return ResponseEntity.ok(topicService.getAll(authorId, moreLiked, categoryId, title, pageable)
                .map(t -> topicMapper.toResumedResponseDto(t, user)));
    }

    @GetMapping("/{topicId}")
    public ResponseEntity<TopicResponseDto> getTopic(@PathVariable Long topicId) {
        return ResponseEntity.ok(topicMapper.toFullResponseDto(topicService.findById(topicId), auth.getUser()));
    }


    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TopicResponseDto> createTopic(@RequestBody @Valid TopicCreateRequestDto dto) {

        var user = auth.getUser();
        var topic = topicService.createTopic(user.getId(), dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(topic.getId())
                .toUri();

        return ResponseEntity.created(location).build();

    }


    @PutMapping("/{topicId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TopicResponseDto> updateTopic(
            @PathVariable Long topicId,
            @RequestBody @Valid TopicEditRequestDto dto) {
        var user = auth.getUser();
        var editedTopic = topicService.updateTopic(topicId, user.getId(), dto);
        return ResponseEntity.ok(topicMapper.toFullResponseDto(editedTopic, user));
    }

    @DeleteMapping("/{topicId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long topicId) {
        topicService.deleteTopic(topicId, auth.getUser().getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{topicId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TopicLikeResponseDto> likeTopic(@PathVariable Long topicId) {
        var totalLikes = topicService.toggleLike(topicId, auth.getUser().getId());
        return ResponseEntity.ok(new TopicLikeResponseDto(topicId, totalLikes));
    }


}
