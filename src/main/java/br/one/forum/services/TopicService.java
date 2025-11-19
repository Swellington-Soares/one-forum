package br.one.forum.services;

import br.one.forum.dtos.TopicCreateRequestDto;
import br.one.forum.dtos.TopicEditMapper;
import br.one.forum.dtos.TopicEditRequestDto;
import br.one.forum.dtos.UpdateTopicRequestDto;
import br.one.forum.entities.Topic;
import br.one.forum.entities.User;
import br.one.forum.exception.InvalidTopicOwnerException;
import br.one.forum.exception.TopicNotFoundException;
import br.one.forum.mappers.TopicResponseMapper;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.repositories.specification.TopicSpecification;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final TopicResponseMapper topicResponseMapper;
    private final TopicEditMapper topicEditMapper;

    public void toggleLike(Topic topic, User user) {
        topic.toggleLike(user);
        topicRepository.save(topic);
    }

    @NonNull
    public Topic findTopicById(int topicId) {
        return topicRepository.findById(topicId).orElseThrow(() -> new TopicNotFoundException(topicId));
    }

    public void deleteTopic(int topicId, User owner) {
        if (owner == null) return;
        topicRepository.deleteTopicByIdAndUserId(topicId, owner.getId());
    }

    public void updateTopic(int topicId, UpdateTopicRequestDto dto) {
        var topic = topicRepository.findById(topicId).orElseThrow(() -> new TopicNotFoundException(topicId));
        topic.setTitle(dto.title() != null ? dto.title() : topic.getTitle());
        topic.setContent(dto.content() != null ? dto.content() : topic.getContent());
        topicRepository.save(topic);
    }


    @Transactional(readOnly = true)
    public Slice<Topic> findAllTopicByUserId(int userId, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return topicRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional
    public Topic createTopic(@NotNull User user, TopicCreateRequestDto dto) {
        var topic = new Topic();
        topic.setTitle(dto.title());
        topic.setContent(dto.content());
        topic.setUser(user);
        dto.categories().forEach(category -> {
            topic.addCategory(category, c -> categoryService.createOrGetCategory(category));
        });
        return topicRepository.save(topic);
    }

    public Page<Topic> getAll(
            Long authorId,
            Boolean moreLiked,
            Pageable pageable
    ) {
        Specification<Topic> spec = Specification.unrestricted();

        if (authorId != null) {
            spec = spec.and(TopicSpecification.byAuthor(authorId));
        }

        if (Boolean.TRUE.equals(moreLiked)) {
            spec = spec.and(TopicSpecification.orderByMoreLiked());
        }

        return topicRepository.findAll(spec, pageable);

    }

    public Topic editTopic(int topicId, TopicEditRequestDto data, User currentLoggedUser) {
        var topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException(topicId));

        if (!topic.getUser().getId().equals(currentLoggedUser.getId()))
            throw new InvalidTopicOwnerException();

        return topicRepository.save( topicEditMapper.partialUpdate(data, topic) );
    }
}
