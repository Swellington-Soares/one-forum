package br.one.forum.repositories;

import br.one.forum.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByAuthorId(Integer userId);
    Page<Comment> findAllByTopicId(int topicId, Pageable pageable);
    Optional<Comment> findCommentByIdAndTopicId(int id, int topicId);
    Page<Comment> findAllByAuthorId(Integer authorId, Pageable pageable);
}