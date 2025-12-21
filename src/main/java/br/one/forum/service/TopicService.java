package br.one.forum.service;

import br.one.forum.dto.request.TopicCreateRequestDto;
import br.one.forum.dto.request.TopicEditRequestDto;
import br.one.forum.entity.Topic;
import br.one.forum.exception.api.EditTopicNotPermittedException;
import br.one.forum.exception.api.TopicNotFoundException;
import br.one.forum.infra.spec.TopicSpecification;
import br.one.forum.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    public Topic findById(Long id) {
        return topicRepository.findById(id).orElseThrow(TopicNotFoundException::new);
    }


    @Transactional
    public Long toggleLike(Long topicId, Long userId) {
        var user = userService.findUserById(userId);
        var topic = findById(topicId);
        if (!topic.isUserAuthor(user)) {
            topic.toggleLike(user);
            topicRepository.save(topic);
        }
        return topic.getLikeCount();
    }

    @Transactional
    public void deleteTopic(Long topicId, Long userId) {
        topicRepository.deleteTopicByIdAndAuthorId(topicId, userId);
    }


    @Transactional
    public Topic createTopic(Long AuthorId, TopicCreateRequestDto dto) {
        var user = userService.findUserById(AuthorId);
        var topic = Topic.builder()
                .title(dto.title())
                .content(dto.content())
                .author(user)
                .build();
        dto.categories().forEach(category -> {
            topic.addCategory(category.trim(),
                    c -> categoryService.createOrGetCategory(category.trim()));
        });
        return topicRepository.save(topic);
    }

    @Transactional(readOnly = true)
    public Slice<Topic> getAll(
            Long authorId,
            Boolean moreLiked,
            Long categoryId,
            String title,
            Pageable pageable) {
        Specification<Topic> spec = Specification.unrestricted();

        if (categoryId != null) {
            spec = spec.and(TopicSpecification.byCategoryId(categoryId));
        }

        if (title != null) {
            spec = spec.and(TopicSpecification.byTitleOrAuthorName(title));
        }

        if (authorId != null) {
            spec = spec.and(TopicSpecification.byAuthorId(authorId));
        }

        if (Boolean.TRUE.equals(moreLiked)) {
            spec = spec.and(TopicSpecification.orderByMoreLiked());
        }

        return topicRepository.findAll(spec, pageable);
    }


    @Transactional
    public Topic updateTopic(Long topicId, Long authorId, TopicEditRequestDto dto) {
        var topic = findById(topicId);
        var user = userService.findUserById(authorId);
        if (!topic.isUserAuthor(user))
            throw new EditTopicNotPermittedException();

        if  (dto.title() != null && !dto.title().isBlank() && !dto.title().equals(topic.getTitle())) {
            topic.setTitle(dto.title());
        }

        if (dto.content() != null && !dto.content().isBlank() && !dto.content().equals(topic.getContent())) {
            topic.setContent(dto.content());
        }

        return topicRepository.save(topic);

    }
}
