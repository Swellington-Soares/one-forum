package br.one.forum.services;

import br.one.forum.dtos.UserPasswordUpdateRequestDto;
import br.one.forum.dtos.UserProfileUpdateRequestDto;
import br.one.forum.dtos.UserRegisterRequestDto;
import br.one.forum.entities.Profile;
import br.one.forum.entities.User;
import br.one.forum.exception.UserAlreadyRegisteredException;
import br.one.forum.exception.UserNotFoundException;
import br.one.forum.exception.UserPasswordNotMatchException;
import br.one.forum.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User findUserById(Integer id, Boolean includeDeleted) {
        if (includeDeleted) {
            return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        }
        return userRepository.findByIdAndDeletedIsFalse(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public User findUserByEmail(String email, Boolean includeDeleted) {
        if (includeDeleted) {
            return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        }
        return userRepository.findByEmailAndDeletedIsFalse(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    public void createUser(UserRegisterRequestDto dto) {
        if (userRepository.existsByEmailIgnoreCase(dto.email()))
            throw new UserAlreadyRegisteredException();

        if (!dto.password().equals(dto.matchPassword()))
            throw new UserPasswordNotMatchException();

        var encodedPassword = passwordEncoder.encode(dto.password());
        User user = new User();
        user.setEmail(dto.email());
        user.setPassword(encodedPassword);
        user.setProfile(new Profile(dto.name(), dto.avatarUrl()));
        userRepository.save(user);
    }

    public void updateUserProfilePhoto(Integer userId, UserProfileUpdateRequestDto dto) {
        var user = findUserById(userId, false);
        user.getProfile().setPhoto(dto.photo());
        userRepository.save(user);
    }

    public void updateUserPassword(Integer userId, UserPasswordUpdateRequestDto dto) {
        var user = findUserById(userId, false);
        if (!passwordEncoder.matches(dto.password(), user.getPassword()))
            throw new UserPasswordNotMatchException();
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    public void updateUserEmailVerified(Integer userId, String token) {
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
}
