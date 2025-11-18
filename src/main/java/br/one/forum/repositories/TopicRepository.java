package br.one.forum.repositories;

import br.one.forum.entities.Topic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer>, JpaSpecificationExecutor<Topic> {
    List<Topic> findByUserId(Integer userId);
    List<Topic> findByTitleContainingIgnoreCase(String title);
    Slice<Topic> findByUserIdOrderByCreatedAtDesc(int userId, Pageable pageable);
    void deleteTopicByIdAndUserId(Integer id, Integer userId);
}