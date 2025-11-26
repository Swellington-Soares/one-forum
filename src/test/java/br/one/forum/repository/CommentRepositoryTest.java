package br.one.forum.repository;

// Running tests against real MariaDB via Testcontainers
import br.one.forum.TestcontainersConfiguration;
import br.one.forum.entities.Comment;
import br.one.forum.entities.Topic;
import br.one.forum.entities.User;
import br.one.forum.repositories.CommentRepository;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes CRUD completos para CommentRepository
 * 
 * Esta classe testa todas as operações CRUD (Create, Read, Update, Delete)
 * do repositório de comentários, incluindo queries customizadas e validação
 * de comportamento de cascata ao deletar tópicos.
 */
@Import(TestcontainersConfiguration.class)
@DataJpaTest
@Transactional
@ActiveProfiles("test")
@Testcontainers
@DisplayName("Comment Repository CRUD Tests")
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    private User author;
    private Topic topic;

    @BeforeEach
    void setUp() {
        // Setup - Criar dados de teste
        author = new User();
        author.setEmail("testauthor@test.com");
        author.setPassword("password123");
        author = userRepository.save(author);

        topic = new Topic();
        topic.setTitle("Test Topic");
        topic.setContent("Topic Content");
        topic.setAuthor(author);
        topic = topicRepository.save(topic);
    }

    // ==================== CREATE TESTS ====================

    @Test
    @DisplayName("CREATE - Deve criar um novo comentário com sucesso")
    void testCreateComment() {
        Comment newComment = new Comment(topic, author, "Test comment content");
        Comment savedComment = commentRepository.save(newComment);

        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getContent()).isEqualTo("Test comment content");
        assertThat(savedComment.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(savedComment.getTopic().getId()).isEqualTo(topic.getId());
        assertThat(commentRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("CREATE - Deve criar múltiplos comentários")
    void testCreateMultipleComments() {
        Comment comment1 = new Comment(topic, author, "First comment");
        Comment comment2 = new Comment(topic, author, "Second comment");
        Comment comment3 = new Comment(topic, author, "Third comment");

        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        assertThat(commentRepository.count()).isEqualTo(3);
    }

    @Test
    @DisplayName("CREATE - Deve persistir timestamps de criação e atualização")
    void testCreateCommentWithTimestamps() {
        Comment newComment = new Comment(topic, author, "Comment with timestamps");
        Comment savedComment = commentRepository.save(newComment);

        assertThat(savedComment.getCreatedAt()).isNotNull();
        assertThat(savedComment.getUpdateAt()).isNotNull();
    }

    // ==================== READ TESTS ====================

    @Test
    @DisplayName("READ - Deve encontrar comentário por ID")
    void testFindCommentById() {
        Comment newComment = new Comment(topic, author, "Test comment");
        Comment savedComment = commentRepository.save(newComment);

        Optional<Comment> foundComment = commentRepository.findById(savedComment.getId());

        assertThat(foundComment).isPresent();
        assertThat(foundComment.get().getContent()).isEqualTo("Test comment");
        assertThat(foundComment.get().getId()).isEqualTo(savedComment.getId());
    }

    @Test
    @DisplayName("READ - Não deve encontrar comentário com ID inexistente")
    void testFindCommentByNonExistentId() {
        Optional<Comment> notFoundComment = commentRepository.findById(999);

        assertThat(notFoundComment).isEmpty();
    }

    @Test
    @DisplayName("READ - Deve encontrar todos os comentários")
    void testFindAllComments() {
        Comment comment1 = new Comment(topic, author, "Comment 1");
        Comment comment2 = new Comment(topic, author, "Comment 2");
        Comment comment3 = new Comment(topic, author, "Comment 3");

        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        List<Comment> allComments = commentRepository.findAll();

        assertThat(allComments).hasSize(3);
        assertThat(allComments).extracting(Comment::getContent)
            .containsExactlyInAnyOrder("Comment 1", "Comment 2", "Comment 3");
    }

    @Test
    @DisplayName("READ - Deve encontrar comentários por autor")
    void testFindCommentsByAuthor() {
        User author2 = new User();
        author2.setEmail("author2@test.com");
        author2.setPassword("password123");
        author2 = userRepository.save(author2);
        
        final Integer author2Id = author2.getId();

        Comment comment1 = new Comment(topic, author, "Comment by author 1");
        Comment comment2 = new Comment(topic, author, "Another comment by author 1");
        Comment comment3 = new Comment(topic, author2, "Comment by author 2");

        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        List<Comment> author1Comments = commentRepository.findByAuthorId(author.getId());
        List<Comment> author2Comments = commentRepository.findByAuthorId(author2Id);

        assertThat(author1Comments).hasSize(2);
        assertThat(author2Comments).hasSize(1);
        assertThat(author1Comments).allMatch(c -> c.getAuthor().getId().equals(author.getId()));
        assertThat(author2Comments).allMatch(c -> c.getAuthor().getId().equals(author2Id));
    }

    @Test
    @DisplayName("READ - Deve encontrar comentários por tópico (paginado)")
    void testFindCommentsByTopic() {
        Comment comment1 = new Comment(topic, author, "Comment 1");
        Comment comment2 = new Comment(topic, author, "Comment 2");
        Comment comment3 = new Comment(topic, author, "Comment 3");

        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        var pagedComments = commentRepository.findAllByTopicId(topic.getId(), PageRequest.of(0, 10));

        assertThat(pagedComments.getTotalElements()).isEqualTo(3);
        assertThat(pagedComments.getContent()).hasSize(3);
        assertThat(pagedComments.getContent()).allMatch(c -> c.getTopic().getId().equals(topic.getId()));
    }

    @Test
    @DisplayName("READ - Deve encontrar comentário por ID e tópico")
    void testFindCommentByIdAndTopicId() {
        Comment newComment = new Comment(topic, author, "Test comment");
        Comment savedComment = commentRepository.save(newComment);

        Optional<Comment> foundComment = commentRepository
            .findCommentByIdAndTopicId(savedComment.getId(), topic.getId());

        assertThat(foundComment).isPresent();
        assertThat(foundComment.get().getId()).isEqualTo(savedComment.getId());
        assertThat(foundComment.get().getTopic().getId()).isEqualTo(topic.getId());
    }

    @Test
    @DisplayName("READ - Não deve encontrar comentário com ID e tópico inválidos")
    void testFindCommentByInvalidIdAndTopicId() {
        Optional<Comment> notFound = commentRepository.findCommentByIdAndTopicId(999, 999);

        assertThat(notFound).isEmpty();
    }

    @Test
    @DisplayName("READ - Deve retornar página vazia para tópico sem comentários")
    void testFindCommentsByTopicWithNone() {
        Topic emptyTopic = new Topic();
        emptyTopic.setTitle("Empty Topic");
        emptyTopic.setContent("No comments here");
        emptyTopic.setAuthor(author);
        emptyTopic = topicRepository.save(emptyTopic);

        var emptyPage = commentRepository.findAllByTopicId(emptyTopic.getId(), PageRequest.of(0, 10));

        assertThat(emptyPage.getTotalElements()).isEqualTo(0);
        assertThat(emptyPage.getContent()).isEmpty();
    }

    // ==================== UPDATE TESTS ====================

    @Test
    @DisplayName("UPDATE - Deve atualizar conteúdo do comentário")
    void testUpdateComment() {
        Comment newComment = new Comment(topic, author, "Original content");
        Comment savedComment = commentRepository.save(newComment);

        savedComment.setContent("Updated content");
        Comment updatedComment = commentRepository.save(savedComment);

        assertThat(updatedComment.getContent()).isEqualTo("Updated content");

        Optional<Comment> retrievedComment = commentRepository.findById(savedComment.getId());
        assertThat(retrievedComment.get().getContent()).isEqualTo("Updated content");
    }

    @Test
    @DisplayName("UPDATE - Deve manter ID e timestamps após atualização")
    void testUpdateCommentMaintainsIdAndTimestamps() {
        Comment newComment = new Comment(topic, author, "Original content");
        Comment savedComment = commentRepository.save(newComment);

        var originalId = savedComment.getId();
        var originalCreatedAt = savedComment.getCreatedAt();

        // Simular pequeno delay para verificar atualização de timestamp
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        savedComment.setContent("Updated content");
        Comment updatedComment = commentRepository.save(savedComment);

        assertThat(updatedComment.getId()).isEqualTo(originalId);
        assertThat(updatedComment.getCreatedAt()).isEqualTo(originalCreatedAt);
        // UpdateAt deve ser posterior (ou igual, dependendo da precisão)
        assertThat(updatedComment.getUpdateAt()).isNotNull();
    }

    // ==================== DELETE TESTS ====================

    @Test
    @DisplayName("DELETE - Deve deletar comentário por ID")
    void testDeleteCommentById() {
        Comment newComment = new Comment(topic, author, "Comment to delete");
        Comment savedComment = commentRepository.save(newComment);
        var commentId = savedComment.getId();

        commentRepository.deleteById(commentId);

        Optional<Comment> deletedComment = commentRepository.findById(commentId);
        assertThat(deletedComment).isEmpty();
        assertThat(commentRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("DELETE - Deve deletar comentário por entidade")
    void testDeleteCommentByEntity() {
        Comment newComment = new Comment(topic, author, "Comment to delete");
        Comment savedComment = commentRepository.save(newComment);

        commentRepository.delete(savedComment);

        Optional<Comment> deletedComment = commentRepository.findById(savedComment.getId());
        assertThat(deletedComment).isEmpty();
    }

    @Test
    @DisplayName("DELETE - Deve deletar múltiplos comentários")
    void testDeleteMultipleComments() {
        Comment comment1 = new Comment(topic, author, "Comment 1");
        Comment comment2 = new Comment(topic, author, "Comment 2");
        Comment comment3 = new Comment(topic, author, "Comment 3");

        Comment saved1 = commentRepository.save(comment1);
        Comment saved2 = commentRepository.save(comment2);
        Comment saved3 = commentRepository.save(comment3);

        commentRepository.deleteById(saved1.getId());
        commentRepository.delete(saved2);
        commentRepository.deleteById(saved3.getId());

        assertThat(commentRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("DELETE - Deletar comentário não deve afetar outros")
    void testDeleteCommentDoesNotAffectOthers() {
        Comment comment1 = new Comment(topic, author, "Comment 1");
        Comment comment2 = new Comment(topic, author, "Comment 2");

        Comment saved1 = commentRepository.save(comment1);
        Comment saved2 = commentRepository.save(comment2);

        commentRepository.deleteById(saved1.getId());

        assertThat(commentRepository.count()).isEqualTo(1);
        assertThat(commentRepository.findById(saved2.getId())).isPresent();
    }

    // ==================== COMPLEX SCENARIO TESTS ====================

    @Test
    @DisplayName("CRUD Completo - Criar, Ler, Atualizar, Deletar")
    void testCompleteCrudFlow() {
        // CREATE
        Comment newComment = new Comment(topic, author, "Initial content");
        Comment savedComment = commentRepository.save(newComment);
        Integer commentId = savedComment.getId();

        assertThat(commentRepository.count()).isEqualTo(1);

        // READ
        Optional<Comment> readComment = commentRepository.findById(commentId);
        assertThat(readComment).isPresent();
        assertThat(readComment.get().getContent()).isEqualTo("Initial content");

        // UPDATE
        readComment.get().setContent("Updated content");
        commentRepository.save(readComment.get());

        Optional<Comment> updatedComment = commentRepository.findById(commentId);
        assertThat(updatedComment.get().getContent()).isEqualTo("Updated content");

        // DELETE
        commentRepository.deleteById(commentId);
        Optional<Comment> deletedComment = commentRepository.findById(commentId);
        assertThat(deletedComment).isEmpty();
        assertThat(commentRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Múltiplos usuários e tópicos")
    void testMultipleUsersAndTopics() {
        User user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setPassword("password123");
        user2 = userRepository.save(user2);

        Topic topic2 = new Topic();
        topic2.setTitle("Topic 2");
        topic2.setContent("Content 2");
        topic2.setAuthor(user2);
        topic2 = topicRepository.save(topic2);

        Comment c1 = new Comment(topic, author, "User1 comment on Topic1");
        Comment c2 = new Comment(topic, user2, "User2 comment on Topic1");
        Comment c3 = new Comment(topic2, author, "User1 comment on Topic2");
        Comment c4 = new Comment(topic2, user2, "User2 comment on Topic2");

        commentRepository.save(c1);
        commentRepository.save(c2);
        commentRepository.save(c3);
        commentRepository.save(c4);

        // Verificar comentários por tópico
        var topic1Comments = commentRepository.findAllByTopicId(topic.getId(), PageRequest.of(0, 10));
        var topic2Comments = commentRepository.findAllByTopicId(topic2.getId(), PageRequest.of(0, 10));

        assertThat(topic1Comments.getTotalElements()).isEqualTo(2);
        assertThat(topic2Comments.getTotalElements()).isEqualTo(2);

        // Verificar comentários por autor
        var user1Comments = commentRepository.findByAuthorId(author.getId());
        var user2Comments = commentRepository.findByAuthorId(user2.getId());

        assertThat(user1Comments).hasSize(2);
        assertThat(user2Comments).hasSize(2);
    }

    // ==================== CASCADE DELETE TESTS ====================

    @Test
    @DisplayName("CASCADE DELETE - Deletar tópico sem comentários")
    void testDeleteTopicWithoutComments() {
        Topic emptyTopic = new Topic();
        emptyTopic.setTitle("Empty Topic");
        emptyTopic.setContent("No comments");
        emptyTopic.setAuthor(author);
        emptyTopic = topicRepository.save(emptyTopic);

        assertThat(topicRepository.count()).isEqualTo(2);
        topicRepository.deleteById(emptyTopic.getId());
        assertThat(topicRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("CASCADE DELETE - Deletar tópico com comentários (deve deletar comentários em cascata)")
    void testDeleteTopicWithCommentsCascades() {
        // Criar comentários usando o constructor sincronizado
        Comment comment1 = new Comment(topic, author, "Comment 1");
        Comment comment2 = new Comment(topic, author, "Comment 2");

        // Salvar comentários PRIMEIRO
        Comment savedComment1 = commentRepository.save(comment1);
        Comment savedComment2 = commentRepository.save(comment2);
        
        // Após salvar, adicionar manualmente à coleção do tópico
        topic.getComments().add(savedComment1);
        topic.getComments().add(savedComment2);
        topicRepository.save(topic);

        assertThat(commentRepository.count()).isEqualTo(2);
        assertThat(topicRepository.count()).isEqualTo(1);

        // Deletar o tópico deve deletar todos os comentários em cascata
        topicRepository.deleteById(topic.getId());

        assertThat(topicRepository.count()).isEqualTo(0);
        assertThat(commentRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("CASCADE DELETE - Deletar tópico com múltiplos comentários")
    void testMultipleCommentsDeletedWithTopic() {
        Comment comment1 = new Comment(topic, author, "Comment 1");
        Comment comment2 = new Comment(topic, author, "Comment 2");
        Comment comment3 = new Comment(topic, author, "Comment 3");

        Comment savedComment1 = commentRepository.save(comment1);
        Comment savedComment2 = commentRepository.save(comment2);
        Comment savedComment3 = commentRepository.save(comment3);
        
        // Adicionar manualmente à coleção do tópico
        topic.getComments().add(savedComment1);
        topic.getComments().add(savedComment2);
        topic.getComments().add(savedComment3);
        topicRepository.save(topic);

        assertThat(commentRepository.count()).isEqualTo(3);

        topicRepository.deleteById(topic.getId());

        assertThat(commentRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("CASCADE DELETE - Deletar apenas comentários do tópico alvo (não afeta outros tópicos)")
    void testDeleteOnlyTargetTopicComments() {
        Topic topic2 = new Topic();
        topic2.setTitle("Topic 2");
        topic2.setContent("Content 2");
        topic2.setAuthor(author);
        topic2 = topicRepository.save(topic2);

        Comment comment1 = new Comment(topic, author, "Comment on Topic 1");
        Comment comment2 = new Comment(topic2, author, "Comment on Topic 2");

        Comment savedComment1 = commentRepository.save(comment1);
        Comment savedComment2 = commentRepository.save(comment2);
        
        // Adicionar manualmente às coleções dos tópicos
        topic.getComments().add(savedComment1);
        topic2.getComments().add(savedComment2);
        topicRepository.save(topic);
        topicRepository.save(topic2);

        assertThat(commentRepository.count()).isEqualTo(2);

        topicRepository.deleteById(topic.getId());

        assertThat(commentRepository.count()).isEqualTo(1);
        assertThat(commentRepository.findAll().get(0).getTopic().getId()).isEqualTo(topic2.getId());
    }
}
