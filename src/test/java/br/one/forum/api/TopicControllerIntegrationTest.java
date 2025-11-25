package br.one.forum.api;

import br.one.forum.TestUtils;
import br.one.forum.controllers.TopicController;
import br.one.forum.dtos.TopicCreateRequestDto;
import br.one.forum.dtos.TopicResponseDto;
import br.one.forum.entities.Category;
import br.one.forum.entities.CurrentUser;
import br.one.forum.entities.Topic;
import br.one.forum.entities.User;
import br.one.forum.exception.TopicNotFoundException;
import br.one.forum.mappers.TopicResponseMapper;
import br.one.forum.seeders.factories.FakeTopicFactory;
import br.one.forum.services.AuthorizationService;
import br.one.forum.services.TokenService;
import br.one.forum.services.TopicService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = TopicController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class TopicControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CurrentUser auth;

    @MockitoBean
    private TopicService topicService;

    @MockitoBean
    private TopicResponseMapper topicResponseMapper;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private AuthorizationService authorizationService;

    private TopicResponseDto topicToResponse(Topic topic) {
        return new TopicResponseDto(
                topic.getId(),
                topic.getTitle(),
                0,
                topic.getContent(),
                false,
                new TopicResponseDto.UserTopicDto(
                        topic.getAuthor().getId(),
                        Instant.now(),
                        new TopicResponseDto.UserTopicDto.UserTopicProfileDto(
                                topic.getAuthor().getProfile().getName(),
                                topic.getAuthor().getProfile().getPhoto()
                        )
                ),
                Instant.now(),
                Instant.now(),
                topic.getCategories()
                        .stream()
                        .map(c -> new TopicResponseDto.TopicCategoryDto(c.getName()))
                        .toList()
        );
    }

    @Test
    @DisplayName("POST /topics should successfully create a new topic and return 201 CREATED.")
    void testCreateTopic_Success() throws Exception {

        User mockUser = TestUtils.mockAuthenticatedUser(50, "SecMOckUser");
        when(auth.getUser()).thenReturn(mockUser);

        Topic mockTopic = FakeTopicFactory.getOne(List.of(mockUser));
        mockTopic.setId(1);
        mockTopic.getCategories().addAll(List.of(
                        new Category("DEV"),
                        new Category("PROGRAMAÇÃO")
                )
        );

        var topicRequestDto = new TopicCreateRequestDto(
                mockTopic.getTitle(),
                mockTopic.getContent(),
                mockTopic.getCategories().stream().map(Category::getName).toList()
        );

        var topicResponseDto = topicToResponse(mockTopic);

        when(topicService.createTopic(mockUser, topicRequestDto))
                .thenReturn(mockTopic);

        when(topicResponseMapper.toDtoExcludeContent(mockTopic, null)).thenReturn(topicResponseDto);


        var requestBody = mapper.writeValueAsString(topicRequestDto);

        mockMvc.perform(post("/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(mockTopic.getId()));

        verify(topicService).createTopic(mockUser, topicRequestDto);
    }

    @Test
    @DisplayName("GET /topics should return paginated list of topics successfully")
    void testGetTopicsSuccess() throws Exception {

        User mockUser = TestUtils.mockAuthenticatedUser(10, "ListUser");
        when(auth.getUser()).thenReturn(mockUser);

        Topic topic1 = FakeTopicFactory.getOne(List.of(mockUser));
        Topic topic2 = FakeTopicFactory.getOne(List.of(mockUser));
        topic1.setId(1);
        topic2.setId(2);

        TopicResponseDto dto1 = topicToResponse(topic1);
        TopicResponseDto dto2 = topicToResponse(topic2);

        Page<Topic> page = new PageImpl<>(List.of(topic1, topic2));

        when(topicService.getAll(
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                any(Pageable.class)
        )).thenReturn(page);

        when(topicResponseMapper.toDtoExcludeContent(topic1, mockUser))
                .thenReturn(dto1);

        when(topicResponseMapper.toDtoExcludeContent(topic2, mockUser))
                .thenReturn(dto2);

        mockMvc.perform(
                        get("/topics")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2));
    }

    @Test
    @DisplayName("GET /topics/{id} should return topic successfully")
    void testGetTopicById() throws Exception {

        User mockUser = TestUtils.mockAuthenticatedUser(20, "UserGET");
        when(auth.getUser()).thenReturn(mockUser);

        Topic topic = FakeTopicFactory.getOne(List.of(mockUser));
        topic.setId(100);

        TopicResponseDto response = topicToResponse(topic);

        when(topicService.findTopicById(100)).thenReturn(topic);
        when(topicResponseMapper.toDto(topic, mockUser)).thenReturn(response);

        mockMvc.perform(get("/topics/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.title").value(topic.getTitle()));
    }

    @Test
    @DisplayName("GET /topics/{id} should return 404 when topic is not found")
    void testGetTopicById_NotFound() throws Exception {

        when(topicService.findTopicById(999))
                .thenThrow(new TopicNotFoundException(999));

        mockMvc.perform(get("/topics/999"))
                .andExpect(status().isNotFound());
    }


    @TestConfiguration
    static class TestMessageSourceConfig {

        @Bean
        public MessageSource messageSource() {
            ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
            ms.setBasename("classpath:i18n");
            ms.setDefaultEncoding("UTF-8");
            ms.setFallbackToSystemLocale(false);
            return ms;
        }
    }

}