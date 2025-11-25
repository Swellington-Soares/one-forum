package br.one.forum.service;

import br.one.forum.dtos.TopicCreateRequestDto;
import br.one.forum.entities.Category;
import br.one.forum.entities.Topic;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.seeders.factories.FakeUserFactory;
import br.one.forum.services.TopicService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles("test")
public class TopicServiceTest {

    @MockitoBean
    private TopicRepository topicRepository;

    @MockitoBean
    private TopicService topicService;


    @Test
    void testIfTopicIsCreatedSuccessfully() {
        var authorMock = FakeUserFactory.getOne();
        authorMock.setId(1);

        var topicData = new TopicCreateRequestDto(
                "Hello World 1",
                "Meu primeiro tópico",
                List.of("DEV", "PROGRAMAÇÃO")
        );

        var topic = new Topic(topicData.title(), topicData.content(), authorMock);
        topic.setId(1);
        topicData.categories().forEach(c ->
                topic.addCategory(new Category(c))
        );

        when(topicService.createTopic(authorMock, topicData)).thenReturn(
                topic
        );

        var topicCreated = topicService.createTopic(authorMock, topicData);

        assertThat(topicCreated.getId()).isEqualTo(1);

    }

}
