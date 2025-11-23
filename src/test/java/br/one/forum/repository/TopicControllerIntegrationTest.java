package br.one.forum.repository;

import br.one.forum.TestcontainersConfiguration;
import br.one.forum.entities.Topic;
import br.one.forum.entities.User;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Testcontainers
class TopicControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper mapper;

    private User author;
    private User otherUser;
    private Topic topic1;

    @BeforeEach
    void setup() {
        topicRepository.deleteAll();
        userRepository.deleteAll();

        author = new User();
        author.setEmail("author@test.com");
        author.setPassword("test_pw");
        userRepository.save(author);

        otherUser = new User();
        otherUser.setEmail("other@test.com");
        otherUser.setPassword("test_pw");
        userRepository.save(otherUser);

        topic1 = new Topic();
        topic1.setTitle("Topic for GET by ID");
        topic1.setContent("Content of the topic for detailed view.");
        topic1.setUser(author);
        topicRepository.save(topic1);

        Topic topic2 = new Topic();
        topic2.setTitle("Second Topic for Listing");
        topic2.setContent("Content of the second topic.");
        topic2.setUser(otherUser);
        topicRepository.save(topic2);
    }

    @Test
    @DisplayName("It should return 200 OK and filter topics by the provided authorId.")
    void testFilterByAuthor() throws Exception {
        String url = String.format("/topics?authorId=%d", author.getId());

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].user.id", is(author.getId())))
                .andExpect(jsonPath("$.content[0].title", is(topic1.getTitle())));
    }

    @Test
    @DisplayName("It should return 200 OK and an empty list when the author has no topics.")
    void testFilterByAuthor_NoTopics() throws Exception {
        User emptyUser = new User();
        emptyUser.setEmail("empty@test.com");
        emptyUser.setPassword("test_pw");
        userRepository.save(emptyUser);

        String url = String.format("/topics?authorId=%d", emptyUser.getId());

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }


    @Test
    @DisplayName("GET /topics - It should return 200 OK and a paginated list of all topics.")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetTopicsSuccess() throws Exception {
        mockMvc.perform(get("/topics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is(topic1.getTitle())))
                .andExpect(jsonPath("$.content[0].content", is(nullValue())));
    }

    @Test
    @DisplayName("GET /topics/{topicId} - \n" +
            "It should return 200 OK and the complete topic by ID.")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetTopicById() throws Exception {
        Integer topicId = topic1.getId();

        mockMvc.perform(get("/topics/{topicId}", topicId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(topicId)))
                .andExpect(jsonPath("$.title", is(topic1.getTitle())))
                .andExpect(jsonPath("$.content", is(topic1.getContent())))
                .andExpect(jsonPath("$.user.id", is(author.getId())))
        ;
    }

    @Test
    @DisplayName("GET /topics/{topicId} - It should return a 404 NOT FOUND error if the topic does not exist.")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetTopicById_NotFound() throws Exception {
        int nonExistentId = 9999;

        mockMvc.perform(get("/topics/{topicId}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}