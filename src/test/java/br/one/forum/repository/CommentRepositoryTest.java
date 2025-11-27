package br.one.forum.repository;

import br.one.forum.TestcontainersConfiguration;
import br.one.forum.entities.Comment;
import br.one.forum.entities.User;
import br.one.forum.repositories.CommentRepository;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.repositories.UserRepository;
import br.one.forum.seeders.factories.FakeTopicFactory;
import br.one.forum.seeders.factories.FakeUserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
@ActiveProfiles("test")
@Testcontainers
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    private User createUser() {
        return userRepository.save(FakeUserFactory.getOne());
    }

    private void createTopic(User author) {
        topicRepository.save(FakeTopicFactory.getOne(List.of(author)));
    }

    @BeforeEach
    void setup() {
        createTopic(createUser());
    }

    @AfterEach
    void cleanDatabase() {
        topicRepository.deleteAll();
        userRepository.deleteAll();
    }
    //teste para criar um comentário
    @Test
    void testCreate() {
        var topic = topicRepository.findAll().getFirst();
        var commentUser = createUser();

        var comment = new Comment();
        comment.setAuthor(commentUser);
        comment.setTopic(topic);
        comment.setContent("Hello World");

        var created = commentRepository.save(comment);

        userRepository.save(commentUser);
        topicRepository.save(topic);

        assertThat(topic).isNotNull();
        assertThat(commentUser).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getContent()).isEqualTo("Hello World");

    }

    //teste para buscar todos os comentários de um usuário
    @Test
    void testFindByAuthorId() {
        var topic = topicRepository.findAll().getFirst();
        var author = createUser();

        var comment1 = new Comment();
        comment1.setAuthor(author);
        comment1.setTopic(topic);
        comment1.setContent("Primeiro comentário");

        var comment2 = new Comment();
        comment2.setAuthor(author);
        comment2.setTopic(topic);
        comment2.setContent("Segundo comentário");

        commentRepository.save(comment1);
        commentRepository.save(comment2);

        var comments = commentRepository.findByAuthorId(author.getId());

        assertThat(comments).isNotNull();
        assertThat(comments).hasSize(2);
        assertThat(comments).allMatch(c -> c.getAuthor().getId().equals(author.getId()));
    }

    @Test
    void testFindByAuthorIdEmpty() {
        var author = createUser();

        var comments = commentRepository.findByAuthorId(author.getId());

        assertThat(comments).isEmpty();
    }

    //teste para buscar todos os comentários de um tópico com paginação
    @Test
    void testFindAllByTopicId() {
        var topic = topicRepository.findAll().getFirst();
        var author = createUser();

        var comment1 = new Comment();
        comment1.setAuthor(author);
        comment1.setTopic(topic);
        comment1.setContent("Comentário 1");

        var comment2 = new Comment();
        comment2.setAuthor(author);
        comment2.setTopic(topic);
        comment2.setContent("Comentário 2");

        commentRepository.save(comment1);
        commentRepository.save(comment2);

        var page = commentRepository.findAllByTopicId(topic.getId(), PageRequest.of(0, 10));

        assertThat(page).isNotNull();
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent()).allMatch(c -> c.getTopic().getId().equals(topic.getId()));
    }


    //teste para buscar um comentário por id e topicId
    @Test
    void testFindCommentByIdAndTopicId() {
        var topic = topicRepository.findAll().getFirst();
        var author = createUser();

        var comment = new Comment();
        comment.setAuthor(author);
        comment.setTopic(topic);
        comment.setContent("Test comment");
        var saved = commentRepository.save(comment);

        var found = commentRepository.findCommentByIdAndTopicId(saved.getId(), topic.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getContent()).isEqualTo("Test comment");
    }

    //teste para buscar comentário que não existe
    @Test
    void testFindCommentByIdAndTopicIdNotFound() {
        var topic = topicRepository.findAll().getFirst();
        var author = createUser();

        var comment = new Comment();
        comment.setAuthor(author);
        comment.setTopic(topic);
        comment.setContent("Test comment");
        commentRepository.save(comment);

        var found = commentRepository.findCommentByIdAndTopicId(999, topic.getId());

        assertThat(found).isEmpty();
    }


    //teste para atualizar um comentário
    @Test
    void testUpdateComment() {
        var topic = topicRepository.findAll().getFirst();
        var author = createUser();

        var comment = new Comment();
        comment.setAuthor(author);
        comment.setTopic(topic);
        comment.setContent("Conteúdo original");
        var saved = commentRepository.save(comment);

        saved.setContent("Conteúdo atualizado");
        var updated = commentRepository.save(saved);

        var found = commentRepository.findById(updated.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo("Conteúdo atualizado");
    }


    //teste para verificar existência de um comentário
    @Test
    void testCommentExistence() {
        var topic = topicRepository.findAll().getFirst();
        var author = createUser();

        var comment = new Comment();
        comment.setAuthor(author);
        comment.setTopic(topic);
        comment.setContent("Test comment");
        var saved = commentRepository.save(comment);

        assertThat(commentRepository.existsById(saved.getId())).isTrue();
    }


}
