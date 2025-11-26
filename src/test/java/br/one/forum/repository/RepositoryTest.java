package br.one.forum.repository;

import br.one.forum.TestcontainersConfiguration;
import br.one.forum.entities.*;
import br.one.forum.repositories.CategoryRepository;
import br.one.forum.repositories.CommentRepository;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//@Disabled
@Import(TestcontainersConfiguration.class)
@DataJpaTest
@Transactional
@ActiveProfiles("test")
@Testcontainers
public class RepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicoRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void testUserAndProfileCrud() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("1234");
        userRepository.save(user);

        Profile profile = new Profile();
        profile.setName("Tester");

        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(userRepository.findById(user.getId())).isPresent();
    }

    @Test
    void testCreateTopicoAndCategory() {
        User user = new User();
        user.setEmail("cat@forum.com");
        user.setPassword("pw");
        userRepository.save(user);

        Topic topico = new Topic();
        topico.setTitle("Primeiro tópico");
        topico.setContent("Conteúdo de teste");
        topico.setAuthor(user);
        topicoRepository.save(topico);

        Category cat = new Category("Java");
        categoryRepository.save(cat);

        cat.getTopics().add(topico);
        categoryRepository.save(cat);

        List<Category> result = categoryRepository.findAll();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().getTopics().contains(topico)).isTrue();
    }

    @Test
    void testCommentCrud() {
        User user = new User();
        user.setEmail("a@a.com");
        user.setPassword("pw");
        userRepository.save(user);

        Topic topico = new Topic();
        topico.setTitle("Titulo");
        topico.setContent("Texto");
        topico.setAuthor(user);
        topicoRepository.save(topico);

        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setTopic(topico);
        comment.setContent("Comentário de teste");
        commentRepository.save(comment);

        assertThat(commentRepository.count()).isEqualTo(1);

        comment.setContent("Comentário editado");
        commentRepository.save(comment);

        assertThat(commentRepository.findAll().getFirst().getContent()).isEqualTo("Comentário editado");
    }

    @Test
    void testCommentCrudComplete() {
        // CREATE - Criar usuário e tópico
        User user = new User();
        user.setEmail("comment@test.com");
        user.setPassword("password123");
        User savedUser = userRepository.save(user);

        Topic topic = new Topic();
        topic.setTitle("Tópico para comentários");
        topic.setContent("Conteúdo do tópico");
        topic.setAuthor(savedUser);
        Topic savedTopic = topicoRepository.save(topic);

        // CREATE - Criar comentário
        Comment comment = new Comment(savedTopic, savedUser, "Meu primeiro comentário");
        Comment savedComment = commentRepository.save(comment);

        assertThat(savedComment.getId()).isNotNull();
        assertThat(commentRepository.count()).isEqualTo(1);

        // READ - Buscar comentário por ID
        var foundComment = commentRepository.findById(savedComment.getId());
        assertThat(foundComment).isPresent();
        assertThat(foundComment.get().getContent()).isEqualTo("Meu primeiro comentário");

        // READ - Buscar comentários por autor
        List<Comment> commentsByAuthor = commentRepository.findByAuthorId(savedUser.getId());
        assertThat(commentsByAuthor).hasSize(1);
        assertThat(commentsByAuthor.getFirst().getAuthor().getId()).isEqualTo(savedUser.getId());

        // READ - Buscar comentários por tópico (paginado)
        var commentsByTopic = commentRepository.findAllByTopicId(savedTopic.getId(), org.springframework.data.domain.Pageable.unpaged());
        assertThat(commentsByTopic.getContent()).hasSize(1);
        assertThat(commentsByTopic.getContent().getFirst().getTopic().getId()).isEqualTo(savedTopic.getId());

        // READ - Buscar comentário por ID e tópico
        var commentByIdAndTopic = commentRepository.findCommentByIdAndTopicId(savedComment.getId(), savedTopic.getId());
        assertThat(commentByIdAndTopic).isPresent();
        assertThat(commentByIdAndTopic.get().getId()).isEqualTo(savedComment.getId());

        // UPDATE - Atualizar comentário
        savedComment.setContent("Comentário atualizado com sucesso");
        Comment updatedComment = commentRepository.save(savedComment);
        assertThat(updatedComment.getContent()).isEqualTo("Comentário atualizado com sucesso");

        // DELETE - Deletar comentário
        commentRepository.deleteById(savedComment.getId());
        assertThat(commentRepository.findById(savedComment.getId())).isEmpty();
        assertThat(commentRepository.count()).isEqualTo(0);
    }

    @Test
    void testCommentCrudMultipleComments() {
        // Setup
        User user1 = new User();
        user1.setEmail("user1@test.com");
        user1.setPassword("pw");
        User savedUser1 = userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setPassword("pw");
        User savedUser2 = userRepository.save(user2);

        Topic topic = new Topic();
        topic.setTitle("Tópico com múltiplos comentários");
        topic.setContent("Conteúdo");
        topic.setAuthor(savedUser1);
        Topic savedTopic = topicoRepository.save(topic);

        // CREATE - Criar múltiplos comentários
        Comment comment1 = new Comment(savedTopic, savedUser1, "Comentário do usuário 1");
        Comment comment2 = new Comment(savedTopic, savedUser2, "Comentário do usuário 2");
        Comment comment3 = new Comment(savedTopic, savedUser1, "Outro comentário do usuário 1");

        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        // READ - Buscar comentários por autor (user1 deve ter 2)
        List<Comment> user1Comments = commentRepository.findByAuthorId(savedUser1.getId());
        assertThat(user1Comments).hasSize(2);

        // READ - Buscar comentários por autor (user2 deve ter 1)
        List<Comment> user2Comments = commentRepository.findByAuthorId(savedUser2.getId());
        assertThat(user2Comments).hasSize(1);

        // READ - Buscar todos os comentários do tópico (deve ter 3)
        var topicComments = commentRepository.findAllByTopicId(savedTopic.getId(), org.springframework.data.domain.Pageable.unpaged());
        assertThat(topicComments.getTotalElements()).isEqualTo(3);

        // DELETE - Deletar um comentário
        commentRepository.delete(comment2);
        assertThat(commentRepository.count()).isEqualTo(2);

        var updatedTopicComments = commentRepository.findAllByTopicId(savedTopic.getId(), org.springframework.data.domain.Pageable.unpaged());
        assertThat(updatedTopicComments.getTotalElements()).isEqualTo(2);
    }

    @Test
    void testCommentNotFoundScenarios() {
        // READ - Buscar comentário inexistente
        var notFound = commentRepository.findById(999);
        assertThat(notFound).isEmpty();

        // READ - Buscar comentário por tópico inexistente
        var notFoundByTopic = commentRepository.findCommentByIdAndTopicId(999, 999);
        assertThat(notFoundByTopic).isEmpty();

        // READ - Buscar comentários de autor inexistente
        List<Comment> emptyComments = commentRepository.findByAuthorId(999);
        assertThat(emptyComments).isEmpty();
    }
}
