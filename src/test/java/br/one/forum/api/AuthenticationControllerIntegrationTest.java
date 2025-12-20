package br.one.forum.api;

import br.one.forum.TestcontainersConfiguration;
import br.one.forum.dtos.request.AuthenticationRequestDto;
import br.one.forum.dtos.response.LoginResponseDto;
import br.one.forum.dtos.request.RefreshTokenRequestDto;
import br.one.forum.dtos.request.UserRegisterRequestDto;
import br.one.forum.entities.User;
import br.one.forum.repositories.UserRepository;
import br.one.forum.services.UserService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Testcontainers
@Transactional
@DisplayName("Testes de Integração: Endpoint /auth")
class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private String validEmail;
    private String validPassword;

    @BeforeEach
    void setUp() {
        // Limpar dados de testes anteriores
        userRepository.deleteAll();

        // Criar usuário de teste
        validEmail = "teste@example.com";
        // Senha de teste apenas - não é um secret real
        validPassword = "TestPassword123!";

        UserRegisterRequestDto registerDto = new UserRegisterRequestDto(
                validEmail,
                validPassword,
                validPassword,
                "Usuário Teste"
        );

        userService.createUser(registerDto);
        testUser = userRepository.findByEmail(validEmail).orElseThrow();
    }

    @Test
    @DisplayName("POST /auth/login deve fazer login com sucesso e retornar tokens")
    void deveFazerLoginComSucesso() throws Exception {
        // Arrange
        AuthenticationRequestDto request = new AuthenticationRequestDto(validEmail, validPassword);

        // Act & Assert
        String responseJson = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Verificar que os tokens são diferentes
        LoginResponseDto response = objectMapper.readValue(responseJson, LoginResponseDto.class);
        assertThat(response.accessToken()).isNotEqualTo(response.refreshToken());

        // Verificar que o refresh token foi salvo no banco
        User userUpdated = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(userUpdated.getRefreshToken()).isNotNull();
        assertThat(userUpdated.getRefreshToken()).isEqualTo(response.refreshToken());
        assertThat(userUpdated.getRefreshTokenExpiration()).isNotNull();
    }

    @Test
    @DisplayName("POST /auth/login deve retornar 400 quando email não existe")
    void deveRetornar400QuandoEmailNaoExiste() throws Exception {
        // Arrange
        AuthenticationRequestDto request = new AuthenticationRequestDto(
                "naoexiste@example.com",
                validPassword
        );

        // Act & Assert - AuthenticationEntryPoint retorna 400 para credenciais inválidas
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/login deve retornar 403 quando senha está incorreta")
    void deveRetornar403QuandoSenhaIncorreta() throws Exception {
        // Arrange
        AuthenticationRequestDto request = new AuthenticationRequestDto(
                validEmail,
                "SenhaIncorreta123!"
        );

        // Act & Assert - Spring Security retorna 403 para BadCredentialsException
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /auth/login deve retornar 400 quando email está vazio")
    void deveRetornar400QuandoEmailVazio() throws Exception {
        // Arrange
        AuthenticationRequestDto request = new AuthenticationRequestDto("", validPassword);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/login deve retornar 403 quando senha está vazia")
    void deveRetornar403QuandoSenhaVazia() throws Exception {
        // Arrange
        AuthenticationRequestDto request = new AuthenticationRequestDto(validEmail, "");

        // Act & Assert - Senha vazia gera BadCredentialsException que retorna 403
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /auth/refresh deve renovar tokens com sucesso")
    void deveRenovarTokensComSucesso() throws Exception {
        // Arrange - Fazer login primeiro para obter refresh token
        AuthenticationRequestDto loginRequest = new AuthenticationRequestDto(validEmail, validPassword);
        String loginResponseJson = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        LoginResponseDto loginResponse = objectMapper.readValue(loginResponseJson, LoginResponseDto.class);
        String originalRefreshToken = loginResponse.refreshToken();

        // Aguardar 1 segundo para garantir que novos tokens tenham exp diferente
        Thread.sleep(1000);

        // Act - Fazer refresh
        RefreshTokenRequestDto refreshRequest = new RefreshTokenRequestDto(originalRefreshToken);
        String refreshResponseJson = mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Assert
        LoginResponseDto refreshResponse = objectMapper.readValue(refreshResponseJson, LoginResponseDto.class);
        assertThat(refreshResponse.accessToken()).isNotEmpty();
        assertThat(refreshResponse.refreshToken()).isNotEmpty();
        // Novo refresh token deve ser diferente do anterior
        assertThat(refreshResponse.refreshToken()).isNotEqualTo(originalRefreshToken);

        // Verificar que o novo refresh token foi salvo no banco
        User userUpdated = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(userUpdated.getRefreshToken()).isEqualTo(refreshResponse.refreshToken());
        assertThat(userUpdated.getRefreshToken()).isNotEqualTo(originalRefreshToken);
    }

    @Test
    @DisplayName("POST /auth/refresh deve retornar 400 quando refresh token está vazio")
    void deveRetornar400QuandoRefreshTokenVazio() throws Exception {
        // Arrange
        RefreshTokenRequestDto request = new RefreshTokenRequestDto("");

        // Act & Assert
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/refresh deve retornar 403 quando refresh token não corresponde ao do banco")
    void deveRetornar403QuandoRefreshTokenNaoCorresponde() throws Exception {
        // Arrange - Fazer login primeiro para obter um token válido
        AuthenticationRequestDto loginRequest = new AuthenticationRequestDto(validEmail, validPassword);
        String loginResponseJson = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        LoginResponseDto loginResponse = objectMapper.readValue(loginResponseJson, LoginResponseDto.class);
        String validToken = loginResponse.refreshToken();

        // Atualizar o refresh token no banco para um valor diferente
        User user = userRepository.findById(testUser.getId()).orElseThrow();
        user.setRefreshToken("token.diferente.no.banco");
        userRepository.save(user);

        // Tentar usar o token válido que não corresponde mais ao do banco
        RefreshTokenRequestDto refreshRequest = new RefreshTokenRequestDto(validToken);

        // Act & Assert - RefreshTokenInvalidException retorna 403
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /auth/refresh deve retornar 400 quando refresh token é inválido/malformado")
    void deveRetornar400QuandoRefreshTokenInvalido() throws Exception {
        // Arrange
        RefreshTokenRequestDto request = new RefreshTokenRequestDto("token.malformado.invalido");

        // Act & Assert - JWTVerificationException retorna 400
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/refresh deve retornar 403 quando refresh token está expirado")
    void deveRetornar403QuandoRefreshTokenExpirado() throws Exception {
        // Arrange - Fazer login primeiro
        AuthenticationRequestDto loginRequest = new AuthenticationRequestDto(validEmail, validPassword);
        String loginResponseJson = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        LoginResponseDto loginResponse = objectMapper.readValue(loginResponseJson, LoginResponseDto.class);
        String refreshToken = loginResponse.refreshToken();

        // Simular token expirado no banco - definir expiração no passado
        User user = userRepository.findById(testUser.getId()).orElseThrow();
        user.setRefreshTokenExpiration(Instant.now().minusSeconds(3600)); // 1 hora atrás
        userRepository.save(user);

        // Act & Assert - ApiTokenExpiredException retorna 403
        RefreshTokenRequestDto refreshRequest = new RefreshTokenRequestDto(refreshToken);
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /auth/login deve retornar 403 quando conta está bloqueada")
    void deveRetornar403QuandoContaBloqueada() throws Exception {
        // Arrange - Bloquear conta do usuário
        User user = userRepository.findById(testUser.getId()).orElseThrow();
        user.setLocked(true);
        userRepository.save(user);

        AuthenticationRequestDto request = new AuthenticationRequestDto(validEmail, validPassword);

        // Act & Assert - LockedException retorna 403
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}

