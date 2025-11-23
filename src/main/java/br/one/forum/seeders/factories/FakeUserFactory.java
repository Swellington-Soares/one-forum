package br.one.forum.seeders.factories;

import br.one.forum.entities.Profile;
import br.one.forum.entities.User;
import br.one.forum.security.PasswordCrypt;
import br.one.forum.utils.DataFaker;

import java.util.ArrayList;
import java.util.List;


public class FakeUserFactory {

    public static User getOne() {
        var user = new User();
        var password = new PasswordCrypt().passwordEncoder().encode("123456");
        user.setEmail(DataFaker.faker().internet().emailAddress());
        user.setPassword(password);
        user.setProfile(
                Profile.builder()
                        .photo(DataFaker.faker().internet().image(200, 200))
                        .name(DataFaker.faker().name().firstName())
                        .build()
        );
        return user;
    }

    public static List<User> getMore(int max) {
        var list = new ArrayList<User>();
        if (max > 0) {
            for (int i = 1; i <= max; i++) {
                list.add(getOne());
            }
        }
        return list;
    }

}
