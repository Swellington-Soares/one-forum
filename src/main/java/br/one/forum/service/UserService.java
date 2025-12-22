package br.one.forum.service;

import br.one.forum.dto.JwtTokenDto;
import br.one.forum.dto.request.UserPasswordUpdateRequestDto;
import br.one.forum.dto.request.UserRegisterRequestDto;
import br.one.forum.entity.Profile;
import br.one.forum.entity.Token;
import br.one.forum.entity.User;
import br.one.forum.exception.api.PasswordNotMatchException;
import br.one.forum.exception.api.PasswordSameAsOldException;
import br.one.forum.exception.api.UserNotFoundException;
import br.one.forum.infra.validation.UserRegisterDomainValidator;
import br.one.forum.repository.UserRepository;
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

    @Value("${api.base-url}")
    private String baseUrl;

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
    public void updateUserPassword(Long userId, UserPasswordUpdateRequestDto dto){
        var user = findUserById(userId);
        if (!dto.passwordMatch().equals(dto.password()))
            throw new PasswordNotMatchException();

        if (passwordEncoder.matches(dto.password(), user.getPassword()))
            throw new PasswordSameAsOldException();

        user.setPassword( passwordEncoder.encode( dto.password()) );
        userRepository.save(user);
    }

    @Transactional
    public void registerUser(UserRegisterRequestDto data) {
        _createUser(data);
        try {
            var requestToken = tokenService.generateEmailToken(data.email(), Token.TokenType.EMAIL_TOKEN);
            var link = baseUrl + "/auth/confirm-account/" + requestToken.getToken();

            emailService.sendHtmlMessage(
                    data.email(),
                    "Ativar conta",
                    "confirm",
                    Map.of("link", link)
            );
        } catch (Exception ignored) {}

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
        var user = findUserById(userId);
        user.setImageUrl(uri);
    }
}
