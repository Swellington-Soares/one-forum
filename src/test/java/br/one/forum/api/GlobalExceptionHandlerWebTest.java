package br.one.forum.api;


import br.one.forum.configuration.MessageSourceConfiguration;
import br.one.forum.controllers.UserController;
import br.one.forum.entities.CurrentUser;
import br.one.forum.exception.GlobalExceptionHandler;
import br.one.forum.exception.api.UserNotFoundException;
import br.one.forum.mappers.UserMapper;
import br.one.forum.repositories.UserRepository;
import br.one.forum.services.AuthorizationService;
import br.one.forum.services.CommentService;
import br.one.forum.services.TokenService;
import br.one.forum.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@WebMvcTest(controllers = UserController.class)
@Import({GlobalExceptionHandler.class, MessageSourceConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
public class GlobalExceptionHandlerWebTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @MockitoBean
    TokenService tokenService;

    @MockitoBean
    AuthorizationService authorizationService;

    @MockitoBean
    UserRepository userRepository;

    @MockitoBean
    private  UserMapper userMapper;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private CurrentUser auth;



    @Test
    @WithMockUser
    @DisplayName("GLOBAL EXCEPTION HANDLE SEM CONFIGURAÇÃO DE IDIOMA")
    void testGlobalExceptionHandlerWithoutLanguage() throws Exception {

        when(userService.findUserById(1, false)).thenThrow(
                new UserNotFoundException(1)
        );

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User with id: 1 not found."));
    }

    @Test
    @DisplayName("GLOBAL EXCEPTION HANDLE COM CONFIGURAÇÃO DE IDIOMA")
    void testGlobalExceptionHandlerWithoutLanguageWithDifferentUserLanguage() throws Exception {

        when(userService.findUserById(1, false)).thenThrow(
                new UserNotFoundException(1)
        );

        mockMvc.perform(get("/users/1")
                        .header("Accept-Language", "pt-BR"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Usuário com ID 1 não existe."));

    }
}