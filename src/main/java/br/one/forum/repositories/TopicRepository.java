package br.one.forum.repositories;

import br.one.forum.entities.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {
    List<Topic> findByUserId(Integer userId);
    List<Topic> findByTitleContainingIgnoreCase(String title);
//    //@Modifying
//    @Query(value = "INSERT INTO likes(topic_id, user_id) VALUES (?1, ?2)", nativeQuery = true)
//    void addLike(int id, int u_id);
//
//
//    @Modifying
//    @Query(value = "DELETE FROM likes WHERE topic_id = ?1 AND user_id = ?2", nativeQuery = true)
//    void removeLike(int id, int u_id);
}