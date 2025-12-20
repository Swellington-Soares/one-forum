package br.one.forum.services;

import br.one.forum.dtos.TokenDto;
import br.one.forum.dtos.request.UserPasswordUpdateRequestDto;
import br.one.forum.dtos.request.UserProfileUpdateRequestDto;
import br.one.forum.dtos.request.UserRegisterRequestDto;
import br.one.forum.entities.Profile;
import br.one.forum.entities.Token;
import br.one.forum.entities.User;
import br.one.forum.exception.api.UserAlreadyRegisteredException;
import br.one.forum.exception.api.UserNotFoundException;
import br.one.forum.exception.api.UserPasswordNotMatchException;
import br.one.forum.repositories.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public final class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Value("${api.api-base}")
    private final String apiBaseUrl = "";

    private final EmailService emailService;

    public User findUserById(Integer id, boolean includeDeleted) {
        if (includeDeleted) {
            return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        }
        return userRepository.findByIdAndDeletedIsFalse(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public User findUserByEmail(String email, boolean includeDeleted) {
        if (includeDeleted) {
            return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        }
        return userRepository.findByEmailAndDeletedIsFalse(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    public void createUser(UserRegisterRequestDto dto) {
        if (userRepository.existsByEmailIgnoreCase(dto.email()))
            throw new UserAlreadyRegisteredException();

//        if (!dto.password().equals(dto.matchPassword()))
//            throw new MethodArgumentNotValidException(
//                    "password",
//
//            );///new UserPasswordNotMatchException();

        var encodedPassword = passwordEncoder.encode(dto.password());
        User user = new User();
        user.setEmail(dto.email());
        user.setPassword(encodedPassword);
        user.setLocked(false);
        user.setEmailVerified(false);
        user.setProfile(
                Profile.builder()
                        .name(dto.name())
                        .build());
        userRepository.save(user);
    }

    public void updateUserProfilePhoto(int userId, UserProfileUpdateRequestDto dto) {
        var user = findUserById(userId, false);
        var oldPhoto = user.getProfile().getPhoto();
        var photo = dto.photo();

        if (oldPhoto.equals(photo)) return;

        user.getProfile().setPhoto(photo);
        userRepository.save(user);
    }

    public void updateUserPassword(int userId, UserPasswordUpdateRequestDto dto) {
        var user = findUserById(userId, false);
        if (!passwordEncoder.matches(dto.password(), user.getPassword()))
            throw new UserPasswordNotMatchException();
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    public void updateUserEmailVerified(int userId, String token) {
        //TODO: DESENVOLVER QUANDO MAIS TARDE
    }

    public void lockUserAccount(int userId) {
        var user = findUserById(userId, false);
        user.setLocked(true);
        userRepository.save(user);
    }

    public void unlockUserAccount(int userId) {
        var user = findUserById(userId, false);
        user.setLocked(false);
        userRepository.save(user);
    }

    public void deleteUser(int userId, boolean force) {
        var user = findUserById(userId, false);
        if (force) {
            userRepository.delete(user);
        } else {
            user.setDeleted(true);
            userRepository.save(user);
        }
    }

    public User findUserByEmailOrNull(String username) {
        return userRepository.findByEmail(username).orElse(null);
    }

    public void updateRefreshToken(User user, TokenDto newRefreshToken) {
        user.setRefreshToken(newRefreshToken.token());
        user.setRefreshTokenExpiration(newRefreshToken.expirationDate());
        userRepository.save(user);
    }

    public void registerUser(UserRegisterRequestDto data) {
        createUser(data);
        try {
            var requestToken = tokenService.generateEmailToken(data.email(), Token.TokenType.EMAIL_TOKEN);
            var link = apiBaseUrl + "/" + requestToken.getToken();

            emailService.sendHtmlMessage(
                    data.email(),
                    "Ativar conta",
                    "confirm",
                    Map.of("link", link)
            );
        } catch (Exception _) {
        }

    }

    public void updateUserProfile(int id, UserProfileUpdateRequestDto data) {
        if (data.photo() != null || data.name() != null) {
            User user = findUserById(id, false);

            if (data.photo() != null && (user.getProfile().getPhoto() == null || !user.getProfile().getPhoto().equals(data.photo()))) {
                user.getProfile().setPhoto(data.photo());
            }

            if (data.name() != null && !user.getProfile().getName().equals(data.name())) {
                user.getProfile().setName(data.name());
            }

            userRepository.save(user);
        }
    }

    public void confirmEmail(String token) {
        var tokenObj = tokenService.validateEmailToken(token);
        var user = findUserByEmail(tokenObj.getEmail(), false);
        if (user.isEmailVerified()) return;
        user.setEmailVerified(true);
        userRepository.save(user);
        tokenService.deleteToken(tokenObj.getToken());
    }
}
