package br.one.forum.services;

import br.one.forum.entities.Topic;
import br.one.forum.entities.User;
import br.one.forum.exception.TopicNotFoundException;
import br.one.forum.repositories.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    public void toggleLike(Topic topic, User user) {
        topic.toggleLike(user);
        topicRepository.save(topic);
    }

    @NonNull
    public Topic findTopicById(int topicId) {
        return topicRepository.findById(topicId).orElseThrow(() -> new TopicNotFoundException(topicId));
    }
}
