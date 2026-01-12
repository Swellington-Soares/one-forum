package br.one.forum.controller;


import br.one.forum.configuration.TestSecurityConfiguration;
import br.one.forum.configuration.UploadImageProperties;
import br.one.forum.dto.request.UserRegisterRequestDto;
import br.one.forum.dto.request.UserUpdateProfileRequestDto;
import br.one.forum.dto.response.UserProfileResponseDto;
import br.one.forum.entity.CurrentUser;
import br.one.forum.entity.Topic;
import br.one.forum.entity.User;
import br.one.forum.infra.I18nUtils;
import br.one.forum.mapper.TopicMapper;
import br.one.forum.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfiguration.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UploadImageProperties imageProperties;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TopicService topicService;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private TopicMapper topicMapper;

    @MockitoBean
    private CurrentUser auth;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private I18nUtils i18nUtils;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private User mockUser() {
        User user = new User();
        user.setId(1L);
        return user;
    }

    @Test
    @WithMockUser
    void shouldReturnAuthenticatedUserProfile() throws Exception {

        UserProfileResponseDto dto = new UserProfileResponseDto(
                1L,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);


        when(auth.getUser()).thenReturn(mockUser());
        when(userService.retrieveUserProfile(1L)).thenReturn(dto);

        mockMvc.perform(get("/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldReturn404WhenUserNotFoundById() throws Exception {

        when(auth.getUser()).thenReturn(null);
        when(userService.retrieveUserProfile(99L))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailWhenRegisteringWithExistingEmail() throws Exception {

        UserRegisterRequestDto request = new UserRegisterRequestDto(
                "existing@email.com",
                "123456",
                "123456",
                "Test test"
        );

        doThrow(new IllegalArgumentException("Email already exists"))
                .when(userService).registerUser(any());

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldFailWhenRegisterPayloadIsMalformed() throws Exception {
        mockMvc.perform( post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{invalid_json}"
                )).andExpect(status().isBadRequest());
    }
    @Test
    void shouldFailWhenUpdatingProfileWithoutAuthentication() throws Exception {

        UserUpdateProfileRequestDto dto = new UserUpdateProfileRequestDto(
                "Novo Nome",
                null
        );


        mockMvc.perform(put("/users/update-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void shouldFailWhenUpdateProfileValidationFails() throws Exception {

        when(auth.getUser()).thenReturn(mockUser());

        UserUpdateProfileRequestDto dto = new UserUpdateProfileRequestDto(
                null, null
        );
        // campos obrigatórios ausentes

        mockMvc.perform(put("/users/update-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldFailWhenServiceThrowsExceptionOnUpdate() throws Exception {

        when(auth.getUser()).thenReturn(mockUser());

        doThrow(new RuntimeException("Unexpected error"))
                .when(userService).updateUserProfile(any(), any());

        UserUpdateProfileRequestDto dto = new UserUpdateProfileRequestDto(
                null, "Hello"
        );


        mockMvc.perform(put("/users/update-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldFailWhenFetchingCommentsFromNonExistingUser() throws Exception {

        when(commentService.findAllByAuthorId(eq(99L), any()))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(get("/users/99/comments"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailWhenTopicMappingFails() throws Exception {

        Topic topic = new Topic();

        when(topicService.getAll(eq(1L), eq(false), isNull(), isNull(), any()))
                .thenReturn(new SliceImpl<>(List.of(topic)));

        when(topicMapper.toResumedResponseDto(any(), isNull()))
                .thenThrow(new RuntimeException("Mapping error"));

        mockMvc.perform(get("/users/1/topics"))
                .andExpect(status().isInternalServerError());
    }



}
