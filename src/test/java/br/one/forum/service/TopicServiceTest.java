package br.one.forum.service;

import br.one.forum.TestcontainersConfiguration;
import br.one.forum.seeders.factories.FakeTopicFactory;
import br.one.forum.seeders.factories.FakeUserFactory;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.repositories.UserRepository;
import br.one.forum.services.TopicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
//@Transactional
@ActiveProfiles("test")
@Testcontainers
public class TopicServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private TopicService topicService;


    @Test
    void testIfSaveLike() {
        var user1 = FakeUserFactory.getOne();
        userRepository.save(user1);

        var topico1 = FakeTopicFactory.getOne(List.of(user1));

        var user2 = FakeUserFactory.getOne();
        userRepository.save(user2);


        topicRepository.save(topico1);
        topicService.toggleLike(topico1, user2);


        assertThat(topico1.getLikeCount()).isEqualTo(1);

    }


}
