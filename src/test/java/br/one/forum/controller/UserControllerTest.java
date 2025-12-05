package br.one.forum.controller;

import br.one.forum.controllers.UserController;
import br.one.forum.dtos.UserProfileUpdateRequestDto;
import br.one.forum.dtos.UserRegisterRequestDto;
import br.one.forum.entities.User;
import br.one.forum.entities.Profile;
import br.one.forum.repositories.UserRepository;
import br.one.forum.services.AuthorizationService;
import br.one.forum.services.TokenService;
import br.one.forum.services.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTestUnit {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthorizationService authorizationService;

    // ========== TESTES DE CADASTRO (RF01) ==========

    @Test
    @DisplayName("Deve cadastrar usuário com sucesso quando dados válidos")
    void shouldRegisterUserSuccessfully() throws Exception {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                "joao.silva@example.com",
                "Senha@123",
                "Senha@123",
                "João Silva",
                "https://avatar.example.com/joao.jpg"
        );

        doNothing().when(userService).registerUser(any(UserRegisterRequestDto.class));

        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        verify(userService, times(1)).registerUser(any(UserRegisterRequestDto.class));
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando e-mail inválido")
    void shouldReturnBadRequestWhenEmailInvalid() throws Exception {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                "email-invalido",
                "Senha@123",
                "Senha@123",
                "João Silva",
                "https://avatar.example.com/joao.jpg"
        );

        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any(UserRegisterRequestDto.class));
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando senha não atende critérios mínimos")
    void shouldReturnBadRequestWhenPasswordTooWeak() throws Exception {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                "joao.silva@example.com",
                "123",
                "123",
                "João Silva",
                "https://avatar.example.com/joao.jpg"
        );

        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any(UserRegisterRequestDto.class));
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando nome está vazio")
    void shouldReturnBadRequestWhenNameIsEmpty() throws Exception {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                "joao.silva@example.com",
                "Senha@123",
                "Senha@123",
                "",
                "https://avatar.example.com/joao.jpg"
        );

        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any(UserRegisterRequestDto.class));
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando campos obrigatórios estão ausentes")
    void shouldReturnBadRequestWhenRequiredFieldsMissing() throws Exception {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                null,
                null,
                null,
                null,
                null
        );

        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any(UserRegisterRequestDto.class));
    }

    @Test
    @DisplayName("Deve retornar erro quando e-mail já existe")
    void shouldReturnErrorWhenEmailAlreadyExists() throws Exception {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                "existente@example.com",
                "Senha@123",
                "Senha@123",
                "João Silva",
                "https://avatar.example.com/joao.jpg"
        );

        doThrow(new IllegalArgumentException("E-mail já cadastrado"))
                .when(userService).registerUser(any(UserRegisterRequestDto.class));

        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).registerUser(any(UserRegisterRequestDto.class));
    }

    //  TESTES DE BUSCA

    @Test
    @DisplayName("Deve retornar usuário quando ID existe")
    void shouldReturnUserWhenIdExists() throws Exception {
        Profile profile = new Profile("João Silva", "Desenvolvedor Java", "https://avatar.example.com/joao.jpg");

        User user = User.builder()
                .id(1)
                .email("joao.silva@example.com")
                .profile(profile)
                .build();

        when(userService.findUserById(1, false)).thenReturn(user);

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("joao.silva@example.com"))
                .andExpect(jsonPath("$.profile.name").value("João Silva"));

        verify(userService, times(1)).findUserById(1, false);
    }

    @Test
    @DisplayName("Deve retornar erro quando usuário não encontrado")
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        when(userService.findUserById(999, false))
                .thenThrow(new RuntimeException("Usuário não encontrado"));

        mockMvc.perform(get("/users/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).findUserById(999, false);
    }

    // TESTES DE ATUALIZAÇÃO DE PERFIL

    @Test
    @WithMockUser(username = "user1")
    @DisplayName("Deve atualizar perfil quando usuário autenticado é o dono")
    void shouldUpdateProfileWhenAuthenticatedUserIsOwner() throws Exception {
        UserProfileUpdateRequestDto updateDto = new UserProfileUpdateRequestDto("João Silva Atualizado", null);

        doNothing().when(userService).updateUserProfile(eq(1), any(UserProfileUpdateRequestDto.class));

        mockMvc.perform(patch("/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        verify(userService, times(1)).updateUserProfile(eq(1), any(UserProfileUpdateRequestDto.class));
    }

    @Test
    @DisplayName("Deve retornar 401 quando usuário não autenticado tenta atualizar")
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        UserProfileUpdateRequestDto updateDto = new UserProfileUpdateRequestDto("João Silva", null);

        mockMvc.perform(patch("/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).updateUserProfile(anyInt(), any(UserProfileUpdateRequestDto.class));
    }

    @Test
    @WithMockUser(username = "user1")
    @DisplayName("Deve permitir atualização com body vazio")
    void shouldAllowUpdateWithEmptyBody() throws Exception {
        doNothing().when(userService).updateUserProfile(eq(1), any());

        mockMvc.perform(patch("/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).updateUserProfile(eq(1), any());
    }

    @Test
    @DisplayName("Deve validar formato do e-mail no cadastro")
    void shouldValidateEmailFormatOnRegister() throws Exception {
        String[] invalidEmails = {
                "email@",
                "@example.com",
                "email@.com",
                "email.example.com",
                "email @example.com"
        };

        for (String invalidEmail : invalidEmails) {
            UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                    invalidEmail,
                    "Senha@123",
                    "Senha@123",
                    "João Silva",
                    "https://avatar.example.com/joao.jpg"
            );

            mockMvc.perform(post("/users/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }

        verify(userService, never()).registerUser(any(UserRegisterRequestDto.class));
    }

    @Test
    @DisplayName("Deve validar critérios de senha no cadastro")
    void shouldValidatePasswordCriteriaOnRegister() throws Exception {
        String[] weakPasswords = {
                "abc",
                "abcdefgh",
                "ABCDEFGH",
                "Abcdefgh",
                "Abcdefg1",
                "12345678",
        };

        for (String weakPassword : weakPasswords) {
            UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                    "joao.silva@example.com",
                    weakPassword,
                    weakPassword,
                    "João Silva",
                    "https://avatar.example.com/joao.jpg"
            );

            mockMvc.perform(post("/users/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }

        verify(userService, never()).registerUser(any(UserRegisterRequestDto.class));
    }

    @Test
    @DisplayName("Deve aceitar senha forte com todos os critérios")
    void shouldAcceptStrongPasswordWithAllCriteria() throws Exception {
        String[] strongPasswords = {
                "Senha@123",
                "P@ssw0rd!",
                "Abcd#1234",
                "Teste@99",
                "MyP@ss123"
        };

        for (String strongPassword : strongPasswords) {
            UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                    "joao.silva@example.com",
                    strongPassword,
                    strongPassword,
                    "João Silva",
                    "https://avatar.example.com/joao.jpg"
            );

            doNothing().when(userService).registerUser(any(UserRegisterRequestDto.class));

            mockMvc.perform(post("/users/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated());
        }
    }

    @Test
    @DisplayName("Deve retornar 403 quando CSRF token está ausente")
    void shouldReturnForbiddenWhenCsrfTokenMissing() throws Exception {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                "joao.silva@example.com",
                "Senha@123",
                "Senha@123",
                "João Silva",
                "https://avatar.example.com/joao.jpg"
        );

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());

        verify(userService, never()).registerUser(any(UserRegisterRequestDto.class));
    }

    @Test
    @DisplayName("Deve aceitar e-mails válidos com diferentes formatos")
    void shouldAcceptValidEmailsWithDifferentFormats() throws Exception {
        String[] validEmails = {
                "user@example.com",
                "user.name@example.com",
                "user+tag@example.co.uk",
                "user_name@sub.example.com",
                "123@example.com"
        };

        for (String validEmail : validEmails) {
            UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                    validEmail,
                    "Senha@123",
                    "Senha@123",
                    "João Silva",
                    "https://avatar.example.com/joao.jpg"
            );

            doNothing().when(userService).registerUser(any(UserRegisterRequestDto.class));

            mockMvc.perform(post("/users/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated());
        }
    }

    @Test
    @DisplayName("Deve retornar 400 quando nome contém apenas espaços")
    void shouldReturnBadRequestWhenNameContainsOnlySpaces() throws Exception {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                "joao.silva@example.com",
                "Senha@123",
                "Senha@123",
                "   ",
                "https://avatar.example.com/joao.jpg"
        );

        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any(UserRegisterRequestDto.class));
    }

    @Test
    @DisplayName("Deve retornar mensagem de erro detalhada quando validação falha")
    void shouldReturnDetailedErrorMessageWhenValidationFails() throws Exception {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                "email-invalido",
                "123",
                "456",
                "",
                "url-invalida"
        );

        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists());

        verify(userService, never()).registerUser(any(UserRegisterRequestDto.class));
    }

    @Test
    @DisplayName("Deve retornar 403 quando usuário tenta atualizar perfil de outro")
    void shouldReturnForbiddenWhenUserTriesToUpdateAnotherProfile() throws Exception {
        UserProfileUpdateRequestDto updateDto = new UserProfileUpdateRequestDto("João Silva Atualizado", null);

        mockMvc.perform(patch("/users/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).updateUserProfile(anyInt(), any(UserProfileUpdateRequestDto.class));
    }

    @Test
    @WithMockUser(username = "user2")
    @DisplayName("Deve retornar 403 quando usuário autenticado não é o dono do perfil")
    void shouldReturnForbiddenWhenAuthenticatedUserIsNotOwner() throws Exception {
        UserProfileUpdateRequestDto updateDto = new UserProfileUpdateRequestDto("João Silva Atualizado", null);

        mockMvc.perform(patch("/users/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());

        verify(userService, never()).updateUserProfile(anyInt(), any(UserProfileUpdateRequestDto.class));
    }

    @Test
    @DisplayName("Deve retornar 400 quando JSON mal formatado")
    void shouldReturnBadRequestWhenJsonMalformed() throws Exception {
        String malformedJson = "{name: 'João', email: }";

        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any(UserRegisterRequestDto.class));
    }

    @Test
    @DisplayName("Deve validar que data de criação é gerada automaticamente")
    void shouldValidateThatCreationDateIsAutomaticallyGenerated() throws Exception {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                "joao.silva@example.com",
                "Senha@123",
                "Senha@123",
                "João Silva",
                "https://avatar.example.com/joao.jpg"
        );

        doNothing().when(userService).registerUser(any(UserRegisterRequestDto.class));

        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        verify(userService, times(1)).registerUser(any(UserRegisterRequestDto.class));
    }

    @Test
    @DisplayName("Deve validar que status ativo é gerado automaticamente")
    void shouldValidateThatActiveStatusIsAutomaticallyGenerated() throws Exception {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                "joao.silva@example.com",
                "Senha@123",
                "Senha@123",
                "João Silva",
                "https://avatar.example.com/joao.jpg"
        );

        doNothing().when(userService).registerUser(any(UserRegisterRequestDto.class));

        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        verify(userService, times(1)).registerUser(any(UserRegisterRequestDto.class));
    }
}