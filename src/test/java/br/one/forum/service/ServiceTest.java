package br.one.forum.service;


import br.one.forum.DataFaker;
import br.one.forum.TestcontainersConfiguration;
import br.one.forum.entities.*;
import br.one.forum.factories.FakeTopicFactory;
import br.one.forum.factories.FakeUserFactory;
import br.one.forum.repositories.CommentRepository;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

//@Disabled
@Import(TestcontainersConfiguration.class)
@DataJpaTest
@Transactional
@ActiveProfiles("test")
@Testcontainers
public class ServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private CommentRepository commentRepository;

    private List<User> users;
    private List<Topic> topics;


    @BeforeEach
    void setup() {
        users = FakeUserFactory.getMore(2);
        userRepository.saveAll(users);
        topics = FakeTopicFactory.getMore(3, users);
        for (int i = 0; i<topics.size(); i++) {
            topics.get(i).setTitle("Tópico " + (i + 1));
        }
        topicRepository.saveAll(topics);
    }

    @Test
    void shouldListAllTopicosOfAUser() {
        List<Topic> topicosDoUser1 = topicRepository.findAll()
                .stream()
                .filter(t -> t.getUser().equals(users.getFirst()))
                .collect(Collectors.toList());

        assertThat(topicosDoUser1).hasSize(3);
        assertThat(topicosDoUser1).extracting(Topic::getTitle)
                .containsExactlyInAnyOrder("Tópico 1", "Tópico 2", "Tópico 3");
    }

    @Test
    void shouldLikeAndUnlikeATopic() {
        // user2 curte topico1
        var topico1 = topics.getFirst();
        var user2 = users.get(1);
        topico1.getLikedBy().add(user2);
        topicRepository.save(topico1);

        Topic updated = topicRepository.findById(topico1.getId()).orElseThrow();
        assertThat(updated.getLikedBy()).contains(user2);

        // user2 remove like
        updated.getLikedBy().remove(user2);
        topicRepository.save(updated);

        Topic refreshed = topicRepository.findById(topico1.getId()).orElseThrow();
        assertThat(refreshed.getLikedBy()).doesNotContain(user2);
    }


    @Test
    void shouldLikeAndUnlikeATopicWithMethod() {
        var topico1 = topics.getFirst();
        var user2 = users.get(1);
        topico1.toggleLike(user2);
        topicRepository.save(topico1);

        Topic updated = topicRepository.findById(topico1.getId()).orElseThrow();
        assertThat(updated.getLikedBy()).contains(user2);
    }

    @Test
    void shouldDetectIfUserAlreadyLikedTopic() {
        var topico2 = topics.getFirst();
        var user1 = users.getFirst();
        topico2.getLikedBy().add(user1);
        topicRepository.save(topico2);

        Topic fetched = topicRepository.findById(topico2.getId()).orElseThrow();
        boolean userLiked = fetched.getLikedBy().stream()
                .anyMatch(u -> u.getId().equals(user1.getId()));

        assertThat(userLiked).isTrue();
    }

    @Test
    void shouldListTopicsByLikeCount() {
        var topico1 = topics.getFirst();
        var topico3 = topics.getLast();
        var user1 = users.getFirst();
        var user2 = users.getLast();
        topico1.getLikedBy().add(user2);
        topico3.getLikedBy().addAll(List.of(user1, user2));

        topicRepository.saveAll(List.of(topico1, topico3));

        List<Topic> all = topicRepository.findAll();
        all.sort(Comparator.comparingInt(t -> -t.getLikedBy().size()));

        Topic maisCurtido = all.getFirst();
        assertThat(maisCurtido.getTitle()).isEqualTo("Tópico 3");
        assertThat(maisCurtido.getLikedBy()).hasSize(2);
    }

    @Test
    void shouldAddAndListComments() {
        var topico1 = topics.getFirst();
        var user1 = users.getFirst();
        var user2 = users.getLast();

        Comment c1 = new Comment(topico1, user2, "Comentário 1");
        Comment c2 = new Comment(topico1, user1, "Comentário 2");

        commentRepository.saveAll(List.of(c1, c2));

        List<Comment> comments = commentRepository.findAll().stream()
                .filter(c -> c.getTopic().equals(topico1))
                .collect(Collectors.toList());

        assertThat(comments).hasSize(2);
        assertThat(comments)
                .extracting(Comment::getContent)
                .containsExactlyInAnyOrder("Comentário 1", "Comentário 2");
    }
}
