package br.one.forum.services;

import br.one.forum.dtos.CreateCommentDto;
import br.one.forum.dtos.UpdateCommentDto;
import br.one.forum.dtos.CommentResponseDto;
import br.one.forum.entities.Comment;
import br.one.forum.repositories.CommentRepository;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private UserRepository userRepository;




    public CommentResponseDto createComment(CreateCommentDto dto) {

        var topic = topicRepository.findById(dto.topicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        var user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var comment = new Comment();
        comment.setTopic(topic);
        comment.setUser(user);
        comment.setContent(dto.content());

        var saved = commentRepository.save(comment);

        return new CommentResponseDto(
                saved.getId(),
                saved.getTopic().getId(),
                saved.getUser().getId(),
                saved.getContent(),
                saved.getCreatedAt()
        );
    }


    public List<CommentResponseDto> listAllComment() {

        return commentRepository.findAll()
                .stream()
                .map(c -> new CommentResponseDto(
                        c.getId(),
                        c.getTopic().getId(),
                        c.getUser().getId(),
                        c.getContent(),
                        c.getCreatedAt()
                ))
                .toList();
    }


    public CommentResponseDto findByIdComment(Integer id) {

        var c = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        return new CommentResponseDto(
                c.getId(),
                c.getTopic().getId(),
                c.getUser().getId(),
                c.getContent(),
                c.getCreatedAt()
        );
    }


    public CommentResponseDto updateComment(Integer id, UpdateCommentDto dto) {

        var comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setContent(dto.content());

        var updated = commentRepository.save(comment);

        return new CommentResponseDto(
                updated.getId(),
                updated.getTopic().getId(),
                updated.getUser().getId(),
                updated.getContent(),
                updated.getCreatedAt()
        );
    }


    public void deleteComment(Integer id) {

        var comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        commentRepository.delete(comment);
    }
}

