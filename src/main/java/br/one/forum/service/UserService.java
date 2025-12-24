package br.one.forum.service;

import br.one.forum.dto.JwtTokenDto;
import br.one.forum.dto.request.UserPasswordUpdateRequestDto;
import br.one.forum.dto.request.UserRegisterRequestDto;
import br.one.forum.dto.request.UserUpdateProfileRequestDto;
import br.one.forum.dto.response.UserProfileResponseDto;
import br.one.forum.entity.Profile;
import br.one.forum.entity.Token;
import br.one.forum.entity.User;
import br.one.forum.exception.api.PasswordNotMatchException;
import br.one.forum.exception.api.PasswordSameAsOldException;
import br.one.forum.exception.api.UserNotFoundException;
import br.one.forum.infra.validation.UserRegisterDomainValidator;
import br.one.forum.mapper.UserMapper;
import br.one.forum.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final AvatarImageService avatarImageService;
    private final List<UserRegisterDomainValidator> userDomainValidators;
    private final UserMapper userMapper;

    @Value("${api.base-url}")
    private String baseApiUrl;

    public User findUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCaseAndDeletedFalse(email).orElseThrow(UserNotFoundException::new);
    }

    public User findUserById(Long id) {
        return userRepository.findByIdAndDeletedIsFalse(id).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public void updateUserRefreshToken(User user, JwtTokenDto refreshToken) {
        user.setRefreshToken(refreshToken.token());
        user.setRefreshTokenExpiration(refreshToken.expirationDate());
        userRepository.save(user);
    }


    protected void _createUser(UserRegisterRequestDto dto) {
        userDomainValidators.forEach(v -> v.validate(dto));
        //process image?
        User user = User.builder()
                .deleted(false)
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .locked(false)
                .emailVerified(false)
                .profile(
                        Profile.builder()
                                .name(dto.name())
                                .build()
                )
                .build();
        userRepository.save(user);
        var imageUrlPath = avatarImageService.getAvatarImageUrl(user.getId());
        user.setImageUrl(imageUrlPath);
        userRepository.save(user);
    }




    @Transactional
    public void registerUser(UserRegisterRequestDto data) {
        _createUser(data);
        try {
            var requestToken = tokenService.generateEmailToken(data.email(), Token.TokenType.EMAIL_TOKEN);
            var link = baseApiUrl + "/auth/confirm-account/" + requestToken.getToken();

            emailService.sendHtmlMessage(
                    data.email(),
                    "Ativar conta",
                    "confirm",
                    Map.of(
                            "link", link
                    )
            );
        } catch (Exception ignored) {
        }

    }


    @Transactional
    public void confirmEmail(String token) {
        var tokenObj = tokenService.validateEmailToken(token);
        var user = findUserByEmail(tokenObj.getEmail());
        if (!user.isEmailVerified()) {
            user.setEmailVerified(true);
            userRepository.save(user);
        }
        tokenService.deleteEmailToken(tokenObj.getToken());
    }

    @Transactional
    public void updateUserProfileImage(Long userId, String uri) {
        userRepository.updatePhotoById(userId, uri);
    }

    public UserProfileResponseDto retrieveUserProfile(Long id) {
        var user = findUserById(id);
        return userMapper.toUserProfileInfoResponseDto(user);
    }

    public void sendPasswordChangeRequest(String email) {
        try {
            var user = findUserByEmail(email);
            var requestToken = tokenService.generateEmailToken(user.getEmail(), Token.TokenType.PASSWORD_TOKEN);
            var link = baseApiUrl + "/auth/change-password/" + requestToken.getToken();
            emailService.sendHtmlMessage(
                    user.getEmail(),
                    "Recuperar senha",
                    "password",
                    Map.of(
                            "link", link
                    )
            );
        } catch (Exception ignored) {}
    }


    public void validatePasswordToken(String token) {
         tokenService.validateEmailToken(token);
    }

    @Transactional
    public void checkAndUpdateUserPassword(String tokenStr, UserPasswordUpdateRequestDto dto) {
        Token token = tokenService.validateEmailToken(tokenStr);
        User user = findUserByEmail(token.getEmail());

        if (!dto.matchPassword().equals(dto.password()))
            throw new PasswordNotMatchException();

        if (passwordEncoder.matches(dto.password(), user.getPassword()))
            throw new PasswordSameAsOldException();

        user.setPassword(passwordEncoder.encode(dto.password()));
        userRepository.save(user);

        tokenService.deleteEmailToken(token.getToken());
    }

    @Transactional
    public void updateUserProfile(User currentUser, UserUpdateProfileRequestDto dto) {
        if (currentUser == null || currentUser.getId() == null) throw new UserNotFoundException();

        var user = findUserById(currentUser.getId());
        var profile = user.getProfile();

        if (!dto.name().equals(profile.getName())) {
            profile.setName(dto.name());
        }

        if (!dto.bio().equals(profile.getBio())) {
            profile.setBio(dto.bio());
        }

        user.setProfile(profile);
    }
}
