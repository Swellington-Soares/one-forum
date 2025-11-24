package br.one.forum.api;

import br.one.forum.TestUtils;
import br.one.forum.controllers.TopicController;
import br.one.forum.dtos.TopicCreateRequestDto;
import br.one.forum.dtos.TopicResponseDto;
import br.one.forum.entities.Category;
import br.one.forum.entities.CurrentUser;
import br.one.forum.entities.Topic;
import br.one.forum.entities.User;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}