package br.one.forum.repositories;

import br.one.forum.entities.EmailToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {
    Optional<EmailToken> findByEmailAndType(String email, EmailToken.TokenType type);
}