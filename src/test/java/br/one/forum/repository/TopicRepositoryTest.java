package br.one.forum.repository;

import br.one.forum.TestcontainersConfiguration;
import br.one.forum.entities.Topic;
import br.one.forum.entities.User;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.repositories.UserRepository;
import jakarta.transaction.Transactional;
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

@Import(TestcontainersConfiguration.class)
@DataJpaTest
@Transactional
@ActiveProfiles("test")
@Testcontainers
public class TopicRepositoryTest {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    private User buildUser(String email) {
        User u = new User();
        u.setEmail(email);
        u.setPassword("123");
        return userRepository.save(u);
    }

    private Topic buildTopic(String title, String content, User author) {
        Topic t = new Topic();
        t.setTitle(title);
        t.setContent(content);
        t.setAuthor(author);
        return t;
    }

    @Test
    void testCreateAndFindById() {
        User author = buildUser("author@test.com");
        Topic topic = buildTopic("Título", "Conteúdo", author);

        topicRepository.save(topic);

        Optional<Topic> found = topicRepository.findById(topic.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Título");
    }

    @Test
    void testUpdateTopic() {
        User author = buildUser("update@test.com");
        Topic topic = buildTopic("Old", "Conteúdo", author);
        topicRepository.save(topic);

        topic.setTitle("Updated");
        topicRepository.save(topic);

        Optional<Topic> found = topicRepository.findById(topic.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Updated");
    }

    @Test
    void testDeleteTopicByIdAndAuthorId() {
        User author = buildUser("delete@test.com");
        Topic topic = buildTopic("To delete", "Conteúdo", author);
        topicRepository.save(topic);

        topicRepository.deleteTopicByIdAndAuthorId(topic.getId(), author.getId());

        assertThat(topicRepository.findById(topic.getId())).isEmpty();
    }

    @Test
    void testFindByAuthorId() {
        User author = buildUser("list@test.com");

        Topic t1 = topicRepository.save(buildTopic("A", "C", author));
        Topic t2 = topicRepository.save(buildTopic("B", "C", author));

        List<Topic> topics = topicRepository.findByAuthorId(author.getId());

        assertThat(topics).hasSize(2);
        assertThat(topics).extracting(Topic::getId)
                .containsExactlyInAnyOrder(t1.getId(), t2.getId());
    }

    @Test
    void testFindByTitleContainingIgnoreCase() {
        User author = buildUser("case@test.com");

        topicRepository.save(buildTopic("JAVA Spring", "C", author));
        topicRepository.save(buildTopic("Estudando Java é bom", "C", author));

        List<Topic> found = topicRepository.findByTitleContainingIgnoreCase("java");

        assertThat(found).hasSize(2);
    }

    @Test
    void testFindByAuthorIdOrderByCreatedAtDesc() {
        User author = buildUser("order@test.com");

        Topic t1 = topicRepository.save(buildTopic("T1", "C", author));
        Topic t2 = topicRepository.save(buildTopic("T2", "C", author));

        var pageable = PageRequest.of(0, 10);
        var slice = topicRepository.findByAuthorIdOrderByCreatedAtDesc(author.getId(), pageable);

        assertThat(slice.getContent()).hasSize(2);
        assertThat(slice.getContent().get(0).getCreatedAt())
                .isAfterOrEqualTo(slice.getContent().get(1).getCreatedAt());
    }

    @Test
    void testFindByIdAndAuthorId() {
        User author = buildUser("owner@test.com");
        User other = buildUser("notowner@test.com");

        Topic topic = topicRepository.save(buildTopic("Test", "C", author));

        Optional<Topic> correct = topicRepository.findByIdAndAuthorId(topic.getId(), author.getId());
        Optional<Topic> wrong = topicRepository.findByIdAndAuthorId(topic.getId(), other.getId());

        assertThat(correct).isPresent();
        assertThat(wrong).isEmpty();
    }
}
