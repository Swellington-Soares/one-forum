package br.one.forum.repositories;

import br.one.forum.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndDeletedIsFalse(Integer id);
    Optional<User> findByEmailAndDeletedIsFalse(String id);

    boolean existsByEmailIgnoreCase(@NonNull String email);
}