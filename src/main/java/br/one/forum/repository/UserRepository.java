package br.one.forum.repository;

import br.one.forum.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdAndDeletedIsFalse(Long id);
    //@Query("SELECT u FROM User u LEFT JOIN FETCH u.likedTopics WHERE u.id = :id")
    //Optional<User> findByIdWithLikedTopics(Integer id);
    Optional<User> findByEmailIgnoreCaseAndDeletedFalse(String email);

    boolean existsByEmailIgnoreCase(String email);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE users SET photo = :uri WHERE id = :id")
    void updatePhotoById(Long id, String uri);
}