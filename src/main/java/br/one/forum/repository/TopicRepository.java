package br.one.forum.repository;

import br.one.forum.entity.Topic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long>, JpaSpecificationExecutor<Topic> {

    List<Topic> findByAuthorId(Long author_id);

    List<Topic> findByTitleContainingIgnoreCase(String title);

    Slice<Topic> findByAuthorIdOrderByCreatedAtDesc(Long author_id);

    void deleteTopicByIdAndAuthorId(Long id, Long author_id);

    Optional<Topic> findByIdAndAuthorId(Long id, Long author_id);

    @Query("""
       SELECT t
       FROM Topic t
       LEFT JOIN t.likedBy l
       GROUP BY t.id
       ORDER BY COUNT(l.id) DESC
       """)
    List<Topic> findAllOrderByLikesDesc();
}