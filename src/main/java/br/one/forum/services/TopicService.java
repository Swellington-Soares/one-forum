package br.one.forum.services;

import br.one.forum.dtos.TopicCreateRequestDto;
import br.one.forum.dtos.UpdateTopicRequestDto;
import br.one.forum.entities.Topic;
import br.one.forum.entities.User;
import br.one.forum.exception.TopicNotFoundException;
import br.one.forum.repositories.CategoryRepository;
import br.one.forum.repositories.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    public void toggleLike(Topic topic, User user) {
        topic.toggleLike(user);
        topicRepository.save(topic);
    }

    @NonNull
    public Topic findTopicById(int topicId) {
        return topicRepository.findById(topicId).orElseThrow(() -> new TopicNotFoundException(topicId));
    }

    public void deleteTopic(int topicId) {
        topicRepository.delete(
                topicRepository.findById(topicId)
                        .orElseThrow(() -> new TopicNotFoundException(topicId))
        );
    }

    public void updateTopic(int topicId, UpdateTopicRequestDto dto) {
        var topic = topicRepository.findById(topicId).orElseThrow(() -> new TopicNotFoundException(topicId));
        topic.setTitle( dto.title() != null ?  dto.title() : topic.getTitle() );
        topic.setContent( dto.content() != null ? dto.content() : topic.getContent() );
        topicRepository.save(topic);
    }


    @Transactional(readOnly = true)
    public Slice<Topic> findAllTopicByUserId(int  userId, int page, int size ) {
        var pagable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return topicRepository.findByUserIdOrderByCreatedAtDesc(userId, pagable);
    }

    @Transactional
    public Topic createTopic(int userId, TopicCreateRequestDto dto){
        var user = userService.findUserById(userId, false);
        var topic = new Topic();
        topic.setTitle(dto.title());
        topic.setContent(dto.content());
        topic.setUser(user);
        dto.categories().forEach(category -> {
            topic.addCategory(category, c -> categoryService.createOrGetCategory(category));
        });
        return topicRepository.save(topic);
    }

}
