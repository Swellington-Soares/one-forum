package br.one.forum.controllers;


import br.one.forum.assemblers.TopicModelAssembler;
import br.one.forum.dtos.TopicResponseDto;
import br.one.forum.mappers.TopicResponseMapper;
import br.one.forum.repositories.UserRepository;
import br.one.forum.services.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class TopicController {

    private final TopicResponseMapper topicResponseMapper;
    private final UserRepository userRepository;
    private final TopicService topicService;
    private final TopicModelAssembler topicModelAssembler;

    @GetMapping("/{id}")
    @Operation(summary = "Busca um tópico pelo ID")
    public EntityModel<TopicResponseDto> getTopic(
            @PathVariable("id") Integer topicId) {
        var topic = topicService.findTopicById( topicId );
        var topicResponse = topicResponseMapper.toDto(topic, null);
        return topicModelAssembler.toModel(topicResponse);
    }
}
