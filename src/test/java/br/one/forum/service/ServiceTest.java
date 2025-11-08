package br.one.forum.service;


import br.one.forum.TestcontainersConfiguration;
import br.one.forum.entities.Comment;
import br.one.forum.entities.Topic;
import br.one.forum.entities.User;
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

    private User user1;
    private User user2;
    private Topic topico1;
    private Topic topico2;
    private Topic topico3;

    @BeforeEach
    void setup() {
        user1 = userRepository.save(new User("user1@forum.com", "pw"));
        user2 = userRepository.save(new User("user2@forum.com", "pw"));

        topico1 = new Topic("Tópico 1", "Conteúdo 1", user1);
        topico2 = new Topic("Tópico 2", "Conteúdo 2", user1);
        topico3 = new Topic("Tópico 3", "Conteúdo 3", user2);

        topicRepository.saveAll(List.of(topico1, topico2, topico3));
    }

    @Test
    void shouldListAllTopicosOfAUser() {
        List<Topic> topicosDoUser1 = topicRepository.findAll()
                .stream()
                .filter(t -> t.getUser().equals(user1))
                .collect(Collectors.toList());

        assertThat(topicosDoUser1).hasSize(2);
        assertThat(topicosDoUser1).extracting(Topic::getTitle)
                .containsExactlyInAnyOrder("Tópico 1", "Tópico 2");
    }

    @Test
    void shouldLikeAndUnlikeATopic() {
        // user2 curte topico1
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
    void shouldDetectIfUserAlreadyLikedTopic() {
        topico2.getLikedBy().add(user1);
        topicRepository.save(topico2);

        Topic fetched = topicRepository.findById(topico2.getId()).orElseThrow();
        boolean userLiked = fetched.getLikedBy().stream()
                .anyMatch(u -> u.getId().equals(user1.getId()));

        assertThat(userLiked).isTrue();
    }

    @Test
    void shouldListTopicsByLikeCount() {

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
        Comment c1 = new Comment(topico1, user2, "Comentário 1");
        Comment c2 = new Comment(topico1, user1, "Comentário 2");

        commentRepository.saveAll(List.of(c1, c2));

        List<Comment> comments = commentRepository.findAll().stream()
                .filter(c -> c.getTopic().equals(topico1))
                .collect(Collectors.toList());

        assertThat(comments).hasSize(2);
        assertThat(comments).extracting(Comment::getContent)
                .containsExactlyInAnyOrder("Comentário 1", "Comentário 2");
    }
}
