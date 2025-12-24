package br.one.forum.service;


import br.one.forum.dto.request.CommentCreateRequestDto;
import br.one.forum.dto.request.UpdateCommentRequestDto;
import br.one.forum.dto.response.CommentResponseDto;
import br.one.forum.entity.Comment;
import br.one.forum.exception.api.CommentCannotBetEditabledException;
import br.one.forum.exception.api.CommentNotFoundException;
import br.one.forum.mapper.CommentMapper;
import br.one.forum.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TopicService topicService;
    private final UserService userService;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentResponseDto createComment(Long topicId, Long userId, CommentCreateRequestDto dto) {
        var topic = topicService.findById(topicId);
        var user = userService.findUserById(userId);
        var comment = Comment.builder()
                .topic(topic)
                .author(user)
                .content(dto.content())
                .build();
        return commentMapper.toResponseDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentResponseDto updateComment(Long id, Long topicId, Long userId, UpdateCommentRequestDto dto) {
        var comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);

        if (!Objects.equals(comment.getTopic().getId(), topicId)) {
            throw new CommentNotFoundException();
        }

        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new CommentCannotBetEditabledException();
        }

        comment.setContent(dto.content());

        return commentMapper.toResponseDto(commentRepository.save(comment));

    }

    @Transactional
    public void deleteComment(Long topicId, Long commentId, Long userId) {
        var comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);

        if (!comment.getTopic().getId().equals(topicId)) return;

        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new CommentCannotBetEditabledException();
        }

        commentRepository.delete(comment);
    }


    @Transactional(readOnly = true)
    public Page<CommentResponseDto> findAllByTopicId(Long topicId, Pageable pageable) {
        return commentRepository.findAllByTopicId(topicId, pageable).map(commentMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public CommentResponseDto findByTopicIdAndCommentId(Long topicId, Long commentId) {
        return commentRepository.findCommentByIdAndTopicId(commentId, topicId)
                .map(commentMapper::toResponseDto)
                .orElseThrow(CommentNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDto> findAllByAuthorId(Long authorId, Pageable pageable) {
        return commentRepository.findAllByAuthorId(authorId, pageable)
                .map(commentMapper::toResponseDto);
    }


}
