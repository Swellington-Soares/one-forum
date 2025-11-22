package br.one.forum.repository;

import br.one.forum.TestcontainersConfiguration;
import br.one.forum.entities.*;
import br.one.forum.repositories.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
    private ProfileRepository profileRepository;
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
        profile.setUser(user);
        profile.setName("Tester");
        profileRepository.save(profile);

        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(profileRepository.findById(user.getId())).isPresent();
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
        topico.setUser(user);
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
        topico.setUser(user);
        topicoRepository.save(topico);

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setTopic(topico);
        comment.setContent("Comentário de teste");
        commentRepository.save(comment);

        assertThat(commentRepository.count()).isEqualTo(1);

        comment.setContent("Comentário editado");
        commentRepository.save(comment);

        assertThat(commentRepository.findAll().getFirst().getContent()).isEqualTo("Comentário editado");
    }
}
