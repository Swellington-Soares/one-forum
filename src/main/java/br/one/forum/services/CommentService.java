package br.one.forum.services;

import br.one.forum.dtos.CreateCommentDto;
import br.one.forum.dtos.UpdateCommentDto;
import br.one.forum.dtos.CommentResponseDto;
import br.one.forum.entities.Comment;
import br.one.forum.exception.CommentNotFoundException;
import br.one.forum.mappers.CommentMapper;
import br.one.forum.repositories.CommentRepository;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TopicService topicService;
    private final UserService  userService;
    private final CommentMapper commentMapper;




    public CommentResponseDto createComment(CreateCommentDto dto) {

        var topic = topicService.findTopicById(dto.topicId());


        var user = userService.findUserById(dto.userId(),false);


        var comment = new Comment();
        comment.setTopic(topic);
        comment.setUser(user);
        comment.setContent(dto.content());

        var saved = commentRepository.save(comment);
        return  commentMapper.toDto(saved);
    }


    public List<CommentResponseDto> listAllComment() {

        return commentRepository.findAll()
                .stream()
                .map(c ->commentMapper.toDto(c)
                )
                .toList();
    }


    public CommentResponseDto findByIdComment(Integer id) {

        var c = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));

        return commentMapper.toDto(c);
    }


    public CommentResponseDto updateComment(Integer id, UpdateCommentDto dto) {

        var comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));

        comment.setContent(dto.content());

        var updated = commentRepository.save(comment);

        return commentMapper.toDto(updated);

    }


    public void deleteComment(Integer id) {

        var comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));

        commentRepository.delete(comment);
    }


}

