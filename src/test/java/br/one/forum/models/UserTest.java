package br.one.forum.models;

import br.one.forum.entities.Profile;
import br.one.forum.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class UserTest {

    @Test
    void testUserToStringMethodWithPasswordProtectedDefinition() {
        var user = new User("david@forum.com", "12345678", new Profile("David", null));
        assertThat(user.getPassword()).isEqualTo("12345678");
        assertThat(user.toString()).contains("password=[PROTECTED]");
    }

}
