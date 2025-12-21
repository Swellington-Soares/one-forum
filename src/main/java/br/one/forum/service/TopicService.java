package br.one.forum.service;

import br.one.forum.dto.request.TopicCreateRequestDto;
import br.one.forum.entity.Topic;
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
    public int toggleLike(Long topicId, Long userId) {
        var user = userService.findUserById(userId);
        var topic = findById(topicId);
        if (!topic.isUserAuthor(user)) {
            topic.toggleLike(user);
            topicRepository.save(topic);
        }
        return topic.getLikeCount();
    }

    @Transactional
    public void deleteTopic(Long topicId) {
        topicRepository.deleteById(topicId);
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


}
