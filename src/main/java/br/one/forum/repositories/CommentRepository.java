package br.one.forum.repositories;

import br.one.forum.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByUserId(Integer userId);

    List<Comment> findByTopicId(Integer topicoId);
}