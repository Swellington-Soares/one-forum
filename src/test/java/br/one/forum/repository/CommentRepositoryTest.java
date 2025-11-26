package br.one.forum.repository;

import br.one.forum.TestcontainersConfiguration;
import br.one.forum.entities.Comment;
import br.one.forum.entities.User;
import br.one.forum.repositories.CommentRepository;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.repositories.UserRepository;
import br.one.forum.seeders.factories.FakeTopicFactory;
import br.one.forum.seeders.factories.FakeUserFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
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


}
