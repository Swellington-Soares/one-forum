package br.one.forum.repositories;

import br.one.forum.entities.Topic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer>, JpaSpecificationExecutor<Topic> {

    List<Topic> findByAuthorId(int authorId);

    List<Topic> findByTitleContainingIgnoreCase(String title);

    Slice<Topic> findByAuthorIdOrderByCreatedAtDesc(int userId, Pageable pageable);

    void deleteTopicByIdAndAuthorId(int id, int userId);

    Optional<Topic> findByIdAndAuthorId(int id, int user_id);
}