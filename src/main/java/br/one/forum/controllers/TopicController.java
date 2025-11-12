package br.one.forum.controllers;


import br.one.forum.dtos.TopicResponseDto;
import br.one.forum.entities.Topic;
import br.one.forum.mappers.TopicResponseMapper;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class TopicController {

    private final TopicResponseMapper topicResponseMapper;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;

    @GetMapping("/{id}")
    TopicResponseDto getTopic(@PathVariable("id") Topic topic) {
        var user = userRepository.findById(11).orElseThrow();
        return topicResponseMapper.toDto(topic, user);
    }

    @GetMapping("/")
    List<TopicResponseDto> getTopic( ) {
        return topicRepository.findAll().stream().map(topic -> topicResponseMapper.toDto(topic, null))
                .toList();
    }


}
