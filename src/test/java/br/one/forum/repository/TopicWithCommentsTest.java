package br.one.forum.repository;

import br.one.forum.entities.Comment;
import br.one.forum.entities.Topic;
import br.one.forum.entities.User;
import br.one.forum.repositories.CommentRepository;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes para validar comportamento de cascata ao deletar tópicos com comentários
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Topic Cascade Delete Tests")
class TopicWithCommentsTest {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    private User author;
    private Topic topic;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setEmail("author@test.com");
        author.setPassword("password123");
        author = userRepository.save(author);

        topic = new Topic();
        topic.setTitle("Topic with Comments");
        topic.setContent("Content");
        topic.setAuthor(author);
        topic = topicRepository.save(topic);
    }

    @Test
    @DisplayName("Deletar tópico SEM comentários funciona normalmente")
    void testDeleteTopicWithoutComments() {
        Integer topicId = topic.getId();
        
        topicRepository.deleteById(topicId);
        
        assertThat(topicRepository.findById(topicId)).isEmpty();
        assertThat(topicRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Deletar tópico COM comentários - cascata remove comentários")
    void testDeleteTopicWithCommentsCascades() {
        // Adicionar comentários ao tópico sem usar setTopic
        Comment comment1 = new Comment(topic, author, "Comment 1");
        Comment comment2 = new Comment(topic, author, "Comment 2");
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        
        // Adicionar manualmente à coleção APÓS persistir
        topic.getComments().add(comment1);
        topic.getComments().add(comment2);

        assertThat(commentRepository.count()).isEqualTo(2);
        Integer topicId = topic.getId();

        // Deletar tópico
        topicRepository.deleteById(topicId);

        // Verificar se tópico foi deletado
        assertThat(topicRepository.findById(topicId)).isEmpty();

        // Verificar se comentários foram deletados em cascata
        assertThat(commentRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Múltiplos comentários são deletados com tópico")
    void testMultipleCommentsDeletedWithTopic() {
        // Criar múltiplos comentários
        for (int i = 1; i <= 5; i++) {
            Comment comment = new Comment(topic, author, "Comment " + i);
            commentRepository.save(comment);
            topic.getComments().add(comment);
        }

        assertThat(commentRepository.count()).isEqualTo(5);

        // Deletar tópico
        topicRepository.deleteById(topic.getId());

        // Todos os comentários devem ter sido deletados
        assertThat(commentRepository.count()).isEqualTo(0);
        assertThat(topicRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Deletar um tópico não afeta comentários de outro tópico")
    void testDeleteOnlyTargetTopicComments() {
        // Criar segundo tópico
        Topic topic2 = new Topic();
        topic2.setTitle("Topic 2");
        topic2.setContent("Content 2");
        topic2.setAuthor(author);
        topic2 = topicRepository.save(topic2);

        // Adicionar comentários a ambos os tópicos
        Comment c1 = new Comment(topic, author, "Comment on Topic 1");
        commentRepository.save(c1);
        topic.getComments().add(c1);

        Comment c2 = new Comment(topic2, author, "Comment on Topic 2");
        commentRepository.save(c2);
        topic2.getComments().add(c2);

        assertThat(topicRepository.count()).isEqualTo(2);
        assertThat(commentRepository.count()).isEqualTo(2);

        // Deletar apenas o primeiro tópico
        topicRepository.deleteById(topic.getId());

        // Verificar que apenas topic1 foi deletado
        assertThat(topicRepository.count()).isEqualTo(1);
        assertThat(topicRepository.findById(topic2.getId())).isPresent();

        // Apenas o comentário do topic1 deve ter sido deletado
        assertThat(commentRepository.count()).isEqualTo(1);
    }
}
