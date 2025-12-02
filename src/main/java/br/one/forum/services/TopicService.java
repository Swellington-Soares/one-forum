package br.one.forum.services;

import br.one.forum.dtos.TopicCreateRequestDto;
import br.one.forum.dtos.TopicEditRequestDto;
import br.one.forum.entities.Topic;
import br.one.forum.entities.User;
import br.one.forum.exception.ActionNotPermittedException;
import br.one.forum.exception.InvalidTopicOwnerException;
import br.one.forum.exception.TopicNotFoundException;
import br.one.forum.mappers.TopicEditMapper;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.repositories.UserRepository;
import br.one.forum.repositories.specification.TopicSpecification;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final CategoryService categoryService;
    private final TopicEditMapper topicEditMapper;
    private final UserRepository userRepository;

    @Transactional
    public int toggleLike(int topicId, User user) {

        var managedUser = userRepository.findByIdWithLikedTopics(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException(topicId));

        if (topic.getAuthor().getId().equals(user.getId()))
            throw new IllegalArgumentException("Usuários não podem curtir seus próprios tópicos");

        topic.toggleLike(managedUser);
        topicRepository.save(topic);

        return topic.getLikeCount();
    }

    @NonNull
    public Topic findTopicById(int topicId) {
        return topicRepository.findById(topicId).orElseThrow(() -> new TopicNotFoundException(topicId));
    }

    @Transactional
    public void deleteTopic(int topicId, User owner) {
        if (owner == null)
            throw new ActionNotPermittedException();
        topicRepository.deleteTopicByIdAndAuthorId(topicId, owner.getId());
    }

    @Transactional(readOnly = true)
    public Slice<Topic> findAllTopicByUserId(int userId, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return topicRepository.findByAuthorIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional
    public Topic createTopic(@NotNull User user, TopicCreateRequestDto dto) {
        var topic = new Topic();
        topic.setTitle(dto.title());
        topic.setContent(dto.content());
        topic.setAuthor(user);
        dto.categories().forEach(category -> {
            topic.addCategory(category, c -> categoryService.createOrGetCategory(category));
        });
        return topicRepository.save(topic);
    }

    public Page<Topic> getAll(
            Long authorId,
            Boolean moreLiked,
            Long categoryId,
            String title,
            Pageable pageable) {
        Specification<Topic> spec = Specification.unrestricted();

        if (categoryId != null) {
            spec = spec.and(TopicSpecification.byCategoryId(categoryId));
        } else if (title != null) {
            spec = spec.and(TopicSpecification.byTitleOrAuthorName(title));
        } else if (authorId != null) {
            spec = spec.and(TopicSpecification.byAuthorId(authorId));
        }

        if (Boolean.TRUE.equals(moreLiked)) {
            spec = spec.and(TopicSpecification.orderByMoreLiked());
        }

        return topicRepository.findAll(spec, pageable);
    }

    public Topic editTopic(int topicId, TopicEditRequestDto data, @Nullable User currentLoggedUser) {

        if (currentLoggedUser == null)
            throw new ActionNotPermittedException();

        var topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException(topicId));

        if (!topic.getAuthor().getId().equals(currentLoggedUser.getId()))
            throw new InvalidTopicOwnerException();

        return topicRepository.save(topicEditMapper.partialUpdate(data, topic));
    }
}
