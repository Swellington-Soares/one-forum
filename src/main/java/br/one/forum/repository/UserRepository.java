package br.one.forum.repository;

import br.one.forum.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndDeletedIsFalse(Long id);
    Optional<User> findByEmailAndDeletedIsFalse(String id);
    boolean existsByEmailIgnoreCase(String email);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.likedTopics WHERE u.id = :id")
    Optional<User> findByIdWithLikedTopics(Integer id);
}