package br.one.forum.controller;

import br.one.forum.dtos.CriarTopicDto;
import br.one.forum.entities.Topic;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/topics")
public class TopicController {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Topic> criarTopic(@RequestBody @Valid CriarTopicDto dto) {

        var user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        var topic = new Topic(dto.title(), dto.content(), user);

        var saved = topicRepository.save(topic);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}

