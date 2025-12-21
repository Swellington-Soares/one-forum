package br.one.forum.repository;

import br.one.forum.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByAuthorId(Long author_id);
    Page<Comment> findAllByTopicId(Long topic_id, Pageable pageable);
    Optional<Comment> findCommentByIdAndTopicId(Long id, Long topic_id);
    Page<Comment> findAllByAuthorId(Long author_id, Pageable pageable);
}