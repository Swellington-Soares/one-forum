package br.one.forum.repository;

import br.one.forum.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByEmailAndType(String email, Token.TokenType type);
    Optional<Token> findByToken(String token);
    void deleteByToken(String token);
    void deleteByTokenIgnoreCase(String token);
}