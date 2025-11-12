package br.one.forum.services;

import br.one.forum.dtos.TopicRegisterDto;
import br.one.forum.dtos.TopicResponseDto;
import br.one.forum.entities.Category;
import br.one.forum.entities.Topic;
import br.one.forum.entities.User;
import br.one.forum.mappers.TopicResponseMapper;
import br.one.forum.repositories.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final TopicResponseMapper topicResponseMapper;


    public void toggleLike(Topic topic, User user) {
        topic.toggleLike(user);
        topicRepository.save(topic);
    }

    public List<TopicResponseDto> getAllTopics(){
        return topicRepository.findAll().stream().map(topico -> topicResponseMapper.toDto(topico, null))
                .toList();
    }


}
