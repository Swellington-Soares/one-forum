package br.one.forum.repository;

import br.one.forum.TestcontainersConfiguration;
import br.one.forum.entities.User;

import br.one.forum.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
@Transactional
@ActiveProfiles("test")
@Testcontainers
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User buildUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("123");
        return user;
    }
    @Test
    void testCreateAndFindById() {
        User user = buildUser("teste@email.com");
        userRepository.save(user);

        Optional<User> userFound = userRepository.findById(user.getId());

        assertThat(userFound.isPresent());
        assertThat(userFound.get().getEmail()).isEqualTo("teste@email.com");
    }

    @Test
    void testUpdateUser() {
        User user = buildUser("update@email.com");
        userRepository.save(user);

        user.setEmail("updated@email.com");
        userRepository.save(user);

        Optional<User> userFound = userRepository.findById(user.getId());

        assertThat(userFound.isPresent());
        assertThat(userFound.get().getEmail()).isEqualTo("updated@email.com");
    }

    @Test
    void testDeleteUser() {
        User user = buildUser("delete@test.com");
        userRepository.save(user);

        userRepository.delete(user);

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }



    @Test
    void testFindByEmail() {
        User user = buildUser("find@test.com");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("find@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(user.getId());
    }

    @Test
    void testExistsByEmailIgnoreCase() {
        User user = buildUser("case@test.com");
        userRepository.save(user);

        assertThat(userRepository.existsByEmailIgnoreCase("CASE@TEST.COM")).isTrue();
        assertThat(userRepository.existsByEmailIgnoreCase("case@test.com")).isTrue();
    }

    @Test
    void testFindByIdAndDeletedIsFalse() {
        User user = buildUser("soft1@test.com");
        user.setDeleted(true);
        userRepository.save(user);

        Optional<User> found = userRepository.findByIdAndDeletedIsFalse(user.getId());

        assertThat(found).isEmpty();
    }

    @Test
    void testFindByEmailAndDeletedIsFalse() {
        User user = buildUser("soft2@test.com");
        user.setDeleted(true);
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmailAndDeletedIsFalse("soft2@test.com");

        assertThat(found).isEmpty();
    }

}
