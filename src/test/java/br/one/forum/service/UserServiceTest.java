package br.one.forum.service;

import br.one.forum.dtos.TokenDto;
import br.one.forum.dtos.UserPasswordUpdateRequestDto;
import br.one.forum.dtos.UserProfileUpdateRequestDto;
import br.one.forum.dtos.UserRegisterRequestDto;
import br.one.forum.entities.Profile;

import br.one.forum.exception.UserAlreadyRegisteredException;
import br.one.forum.exception.UserNotFoundException;
import br.one.forum.exception.UserPasswordNotMatchException;
import br.one.forum.repositories.UserRepository;
import br.one.forum.seeders.factories.FakeUserFactory;
import br.one.forum.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUserSuccessfully() {
        var user = FakeUserFactory.getOne();
        user.setId(10);

        when(userRepository.save(user)).thenReturn(user);

        assertThat(user.getId()).isEqualTo(10);
        assertThat(user).isNotNull();
    }

    @Test
    void findUserById() {
        var user = FakeUserFactory.getOne();
        user.setId(1);
        user.setDeleted(false);

        when(userRepository.findByIdAndDeletedIsFalse(1))
                .thenReturn(Optional.of(user));

        var found = userService.findUserById(1, false);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1);
        assertThat(found.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void findUserByEmail(){
        var user = FakeUserFactory.getOne();
        user.setEmail("fulanoDaSilva@gmail.com");
        user.setId(1);
        user.setDeleted(false);
        when(userRepository.findByEmailAndDeletedIsFalse("fulanoDaSilva@gmail.com")).thenReturn(Optional.of(user));

        var found = userService.findUserByEmail("fulanoDaSilva@gmail.com", false);
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("fulanoDaSilva@gmail.com");
        assertThat(found.getId()).isEqualTo(1);
    }

    @Test
    void updateUserProfilePhoto() {
        var user = FakeUserFactory.getOne();
        user.setId(1);
        user.setDeleted(false);

        user.setProfile(new Profile("Fulano", "FotoAntiga.png", "Hello world"));

        var dto = new UserProfileUpdateRequestDto("","FotoNova.png");


        when(userRepository.findByIdAndDeletedIsFalse(1))
                .thenReturn(Optional.of(user));


        when(userRepository.save(user)).thenReturn(user);


        userService.updateUserProfilePhoto(1, dto);


        assertThat(user.getProfile().getPhoto()).isEqualTo("FotoNova.png");
    }


    @Test
    void shouldThrow_WhenEmailAlreadyExists() {
        var dto = new UserRegisterRequestDto(
                "email@teste.com", "123456", "123456", "Fulano", "foto.png"
        );

        when(userRepository.existsByEmailIgnoreCase("email@teste.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(UserAlreadyRegisteredException.class);
    }

    @Test
    void shouldThrow_WhenPasswordDoesNotMatch() {
        var dto = new UserRegisterRequestDto(
                "x@x.com", "123", "000", "Name", "avatar.png"
        );

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(UserPasswordNotMatchException.class);
    }


    @Test
    void shouldUpdatePasswordSuccessfully() {
        var user = FakeUserFactory.getOne();
        user.setId(1);
        user.setPassword("oldEncoded");

        var dto = new UserPasswordUpdateRequestDto("old", "newPass");

        when(userRepository.findByIdAndDeletedIsFalse(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "oldEncoded")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("newEncoded");

        userService.updateUserPassword(1, dto);

        verify(userRepository).save(user);
        assertThat(user.getPassword()).isEqualTo("newEncoded");
    }

    @Test
    void shouldThrow_WhenOldPasswordDoesNotMatch() {
        var user = FakeUserFactory.getOne();
        user.setId(1);
        user.setPassword("encodedOld");

        var dto = new UserPasswordUpdateRequestDto("wrongOld", "newPass");

        when(userRepository.findByIdAndDeletedIsFalse(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongOld", "encodedOld")).thenReturn(false);

        assertThatThrownBy(() -> userService.updateUserPassword(1, dto))
                .isInstanceOf(UserPasswordNotMatchException.class);
    }


    @Test
    void shouldUpdateUserProfile() {
        var user = FakeUserFactory.getOne();
        user.setId(1);
        user.setProfile(new Profile("Antigo", "foto1.png", "bio"));

        var dto = new UserProfileUpdateRequestDto( "Novo Nome", "foto2.png");

        when(userRepository.findByIdAndDeletedIsFalse(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.updateUserProfile(1, dto);

        assertThat(user.getProfile().getPhoto()).isEqualTo("foto2.png");
        assertThat(user.getProfile().getName()).isEqualTo("Novo Nome");
    }


    @Test
    void shouldLockUser() {
        var user = FakeUserFactory.getOne();
        user.setId(1);

        when(userRepository.findByIdAndDeletedIsFalse(1)).thenReturn(Optional.of(user));

        userService.lockUserAccount(1);

        assertThat(user.isLocked()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    void shouldUnlockUser() {
        var user = FakeUserFactory.getOne();
        user.setId(1);
        user.setLocked(true);

        when(userRepository.findByIdAndDeletedIsFalse(1)).thenReturn(Optional.of(user));

        userService.unlockUserAccount(1);

        assertThat(user.isLocked()).isFalse();
        verify(userRepository).save(user);
    }


    @Test
    void shouldSoftDeleteUser() {
        var user = FakeUserFactory.getOne();
        user.setId(1);

        when(userRepository.findByIdAndDeletedIsFalse(1)).thenReturn(Optional.of(user));

        userService.deleteUser(1, false);

        assertThat(user.isDeleted()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    void shouldHardDeleteUser() {
        var user = FakeUserFactory.getOne();
        user.setId(1);

        when(userRepository.findByIdAndDeletedIsFalse(1)).thenReturn(Optional.of(user));

        userService.deleteUser(1, true);

        verify(userRepository, times(1)).delete(user);
    }


    @Test
    void shouldReturnUser_WhenEmailExists() {
        var user = FakeUserFactory.getOne();
        user.setEmail("abc@x.com");

        when(userRepository.findByEmail("abc@x.com")).thenReturn(Optional.of(user));

        var result = userService.findUserByEmailOrNull("abc@x.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("abc@x.com");
    }

    @Test
    void shouldReturnNull_WhenEmailNotFound() {
        when(userRepository.findByEmail("nope@mail.com")).thenReturn(Optional.empty());

        var result = userService.findUserByEmailOrNull("nope@mail.com");

        assertThat(result).isNull();
    }


    @Test
    void shouldUpdateRefreshToken() {
        var user = FakeUserFactory.getOne();
        var expiration = Instant.now().plusSeconds(86400); // +1 dia
        var token = new TokenDto("token123", expiration);

        userService.updateRefreshToken(user, token);

        assertThat(user.getRefreshToken()).isEqualTo("token123");
        assertThat(user.getRefreshTokenExpiration()).isEqualTo(expiration);
        verify(userRepository).save(user);
    }



    @Test
    void shouldThrow_WhenUserNotFoundById() {
        when(userRepository.findByIdAndDeletedIsFalse(12)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserById(12, false))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldThrow_WhenUserNotFoundByEmail() {
        when(userRepository.findByEmailAndDeletedIsFalse("x@x.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserByEmail("x@x.com", false))
                .isInstanceOf(UserNotFoundException.class);
    }
}




