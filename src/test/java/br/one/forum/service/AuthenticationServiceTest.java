package br.one.forum.service;

import br.one.forum.dtos.AuthenticationRequestDto;
import br.one.forum.dtos.LoginResponseDto;
import br.one.forum.dtos.TokenDto;
import br.one.forum.entities.Profile;
import br.one.forum.entities.User;
import br.one.forum.exception.ApiTokenExpiredException;
import br.one.forum.exception.RefreshTokenInvalidException;
import br.one.forum.exception.UserNotFoundException;
import br.one.forum.exception.UserPasswordNotMatchException;
import br.one.forum.security.AppUserDetails;
import br.one.forum.services.AuthenticationService;
import br.one.forum.services.TokenService;
import br.one.forum.services.UserService;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AuthenticationService")
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;
    private Profile profile;
    private AuthenticationRequestDto validRequest;
    private String email;
    private String password;
    private String encodedPassword;

    @BeforeEach
    void setUp() {
        email = "teste@example.com";
        password = "senha123";
        encodedPassword = "$2a$10$encodedPasswordHash";

        profile = Profile.builder()
                .name("Teste User")
                .photo(null)
                .bio(null)
                .build();

        user = new User();
        user.setId(1);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setProfile(profile);
        user.setEmailVerified(true);
        user.setLocked(false);

        validRequest = new AuthenticationRequestDto(email, password);
    }

    @Test
    @DisplayName("Deve fazer login com sucesso e retornar tokens")
    void deveFazerLoginComSucesso() {
        // Arrange
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new AppUserDetails(user),
                null,
                null
        );

        TokenDto accessToken = new TokenDto("access-token-123", Instant.now().plusSeconds(900));
        TokenDto refreshToken = new TokenDto("refresh-token-456", Instant.now().plusSeconds(604800));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(passwordEncoder.matches(password, encodedPassword))
                .thenReturn(true);
        when(tokenService.generateToken(user))
                .thenReturn(accessToken);
        when(tokenService.generateRefreshToken(user))
                .thenReturn(refreshToken);

        // Act
        LoginResponseDto response = authenticationService.login(validRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("access-token-123");
        assertThat(response.refreshToken()).isEqualTo("refresh-token-456");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(passwordEncoder).matches(password, encodedPassword);
        verify(tokenService).generateToken(user);
        verify(tokenService).generateRefreshToken(user);
        verify(userService).updateRefreshToken(eq(user), eq(refreshToken));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não for encontrado no login")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Arrange
        User nullUser = null;
        AppUserDetails appUserDetails = new AppUserDetails(nullUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                appUserDetails,
                null,
                null
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.login(validRequest))
                .isInstanceOf(UserNotFoundException.class);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(tokenService, never()).generateToken(any());
        verify(tokenService, never()).generateRefreshToken(any());
        verify(userService, never()).updateRefreshToken(any(), any(TokenDto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha não corresponder")
    void deveLancarExcecaoQuandoSenhaNaoCorresponde() {
        // Arrange
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new AppUserDetails(user),
                null,
                null
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(passwordEncoder.matches(password, encodedPassword))
                .thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.login(validRequest))
                .isInstanceOf(UserPasswordNotMatchException.class);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(passwordEncoder).matches(password, encodedPassword);
        verify(tokenService, never()).generateToken(any());
        verify(tokenService, never()).generateRefreshToken(any());
        verify(userService, never()).updateRefreshToken(any(), any(TokenDto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando autenticação falhar")
    void deveLancarExcecaoQuandoAutenticacaoFalhar() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.login(validRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Bad credentials");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(tokenService, never()).generateToken(any());
        verify(tokenService, never()).generateRefreshToken(any());
        verify(userService, never()).updateRefreshToken(any(), any(TokenDto.class));
    }

    @Test
    @DisplayName("Deve renovar tokens com refresh token válido")
    void deveRenovarTokensComRefreshTokenValido() {
        // Arrange
        String refreshTokenString = "valid-refresh-token";
        Instant expiration = Instant.now().plusSeconds(604800);
        user.setRefreshToken(refreshTokenString);
        user.setRefreshTokenExpiration(expiration);

        DecodedJWT decodedJWT = org.mockito.Mockito.mock(DecodedJWT.class);
        when(decodedJWT.getSubject()).thenReturn(email);

        TokenDto newAccessToken = new TokenDto("new-access-token", Instant.now().plusSeconds(900));
        TokenDto newRefreshToken = new TokenDto("new-refresh-token", Instant.now().plusSeconds(604800));

        when(tokenService.validateRefreshToken(refreshTokenString))
                .thenReturn(decodedJWT);
        when(userService.findUserByEmail(email, false))
                .thenReturn(user);
        when(tokenService.generateToken(user))
                .thenReturn(newAccessToken);
        when(tokenService.generateRefreshToken(user))
                .thenReturn(newRefreshToken);

        // Act
        LoginResponseDto response = authenticationService.refreshToken(refreshTokenString);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo("new-refresh-token");

        verify(tokenService).validateRefreshToken(refreshTokenString);
        verify(userService).findUserByEmail(email, false);
        verify(tokenService).generateToken(user);
        verify(tokenService).generateRefreshToken(user);
        verify(userService).updateRefreshToken(eq(user), eq(newRefreshToken));
    }

    @Test
    @DisplayName("Deve lançar exceção quando refresh token não corresponder ao do banco")
    void deveLancarExcecaoQuandoRefreshTokenNaoCorresponde() {
        // Arrange
        String refreshTokenString = "invalid-refresh-token";
        String differentToken = "different-token-in-db";
        Instant expiration = Instant.now().plusSeconds(604800);
        user.setRefreshToken(differentToken);
        user.setRefreshTokenExpiration(expiration);

        DecodedJWT decodedJWT = org.mockito.Mockito.mock(DecodedJWT.class);
        when(decodedJWT.getSubject()).thenReturn(email);

        when(tokenService.validateRefreshToken(refreshTokenString))
                .thenReturn(decodedJWT);
        when(userService.findUserByEmail(email, false))
                .thenReturn(user);

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.refreshToken(refreshTokenString))
                .isInstanceOf(RefreshTokenInvalidException.class);

        verify(tokenService).validateRefreshToken(refreshTokenString);
        verify(userService).findUserByEmail(email, false);
        verify(tokenService, never()).generateToken(any());
        verify(tokenService, never()).generateRefreshToken(any());
        verify(userService, never()).updateRefreshToken(any(), any(TokenDto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando refresh token estiver expirado")
    void deveLancarExcecaoQuandoRefreshTokenExpirado() {
        // Arrange
        String refreshTokenString = "expired-refresh-token";
        Instant expiredTime = Instant.now().minusSeconds(3600); // 1 hora atrás
        user.setRefreshToken(refreshTokenString);
        user.setRefreshTokenExpiration(expiredTime);

        DecodedJWT decodedJWT = org.mockito.Mockito.mock(DecodedJWT.class);
        when(decodedJWT.getSubject()).thenReturn(email);

        when(tokenService.validateRefreshToken(refreshTokenString))
                .thenReturn(decodedJWT);
        when(userService.findUserByEmail(email, false))
                .thenReturn(user);

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.refreshToken(refreshTokenString))
                .isInstanceOf(ApiTokenExpiredException.class);

        verify(tokenService).validateRefreshToken(refreshTokenString);
        verify(userService).findUserByEmail(email, false);
        verify(tokenService, never()).generateToken(any());
        verify(tokenService, never()).generateRefreshToken(any());
        verify(userService, never()).updateRefreshToken(any(), any(TokenDto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando refresh token for inválido")
    void deveLancarExcecaoQuandoRefreshTokenInvalido() {
        // Arrange
        String invalidToken = "invalid-token";

        when(tokenService.validateRefreshToken(invalidToken))
                .thenThrow(new RuntimeException("Invalid token"));

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.refreshToken(invalidToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid token");

        verify(tokenService).validateRefreshToken(invalidToken);
        verify(userService, never()).findUserByEmail(anyString(), eq(false));
    }
}

