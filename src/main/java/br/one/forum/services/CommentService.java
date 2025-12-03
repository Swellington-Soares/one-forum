package br.one.forum.services;

import br.one.forum.dtos.request.CommentCreateRequestDto;
import br.one.forum.dtos.request.UpdateCommentRequestDto;
import br.one.forum.dtos.response.CommentResponseDto;
import br.one.forum.dtos.response.UserCommentResponseDto;
import br.one.forum.entities.Comment;
import br.one.forum.entities.User;
import br.one.forum.exception.api.CommentCannotBeEditableByCurrentUserException;
import br.one.forum.exception.api.CommentNotFoundException;
import br.one.forum.mappers.CommentMapper;
import br.one.forum.mappers.UserCommentMapper;
import br.one.forum.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TopicService topicService;
    private final UserService userService;
    private final CommentMapper commentMapper;
    private final UserCommentMapper userCommentMapper;

    public CommentResponseDto createComment(int topicId, User user, CommentCreateRequestDto dto) {
        var topic = topicService.findTopicById(topicId);
        var comment = new Comment();
        comment.setContent(dto.content());
        comment.setTopic(topic);
        comment.setAuthor(user);
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }



    public CommentResponseDto updateComment(int userId, int topicId, int id, UpdateCommentRequestDto dto) {
        var comment = commentRepository.findCommentByIdAndTopicId(id, topicId)
                .orElseThrow(() -> new CommentNotFoundException(id));
        if (comment.getAuthor().getId() != userId)
            throw new CommentCannotBeEditableByCurrentUserException();
        comment.setContent(dto.content());
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    public void deleteComment(int userId, int topicId, int id) {
        var comment = commentRepository.findCommentByIdAndTopicId(id, topicId)
                .orElseThrow(() -> new CommentNotFoundException(id));
        if (comment.getAuthor().getId() != userId)
            throw new CommentCannotBeEditableByCurrentUserException();
        commentRepository.delete(comment);
    }


    public Page<CommentResponseDto> findAllByTopicId(int topicId, Pageable pageable) {
        return commentRepository.findAllByTopicId(topicId, pageable).map(commentMapper::toDto);
    }

    public CommentResponseDto findByTopicIdAndCommentId(int topicId, int id) {
        return commentRepository.findCommentByIdAndTopicId(id, topicId)
                .map(commentMapper::toDto)
                .orElseThrow(() -> new CommentNotFoundException(id));
    }

    public Page<UserCommentResponseDto> findAllByAuthorId(int authorId, Pageable pageable) {
        return commentRepository.findAllByAuthorId(authorId, pageable)
                .map(userCommentMapper::toDto);
    }
}

