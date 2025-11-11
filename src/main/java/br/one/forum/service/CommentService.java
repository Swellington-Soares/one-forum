package br.one.forum.service;

import br.one.forum.dtos.CriarCommentDto;
import br.one.forum.dtos.UpdateCommentDto;
import br.one.forum.dtos.CommentResponseDto;
import br.one.forum.entities.Comment;
import br.one.forum.repositories.CommentRepository;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          TopicRepository topicRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }


    public CommentResponseDto criarComentario(CriarCommentDto dto) {

        var topic = topicRepository.findById(dto.topicId())
                .orElseThrow(() -> new RuntimeException("Topic não encontrado"));

        var user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new RuntimeException("User não encontrado"));

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


    public List<CommentResponseDto> listarTodos() {

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


    public CommentResponseDto buscarPorId(Integer id) {

        var c = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado"));

        return new CommentResponseDto(
                c.getId(),
                c.getTopic().getId(),
                c.getUser().getId(),
                c.getContent(),
                c.getCreatedAt()
        );
    }


    public CommentResponseDto atualizar(Integer id, UpdateCommentDto dto) {

        var comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado"));

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


    public void deletar(Integer id) {

        var comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado"));

        commentRepository.delete(comment);
    }
}

