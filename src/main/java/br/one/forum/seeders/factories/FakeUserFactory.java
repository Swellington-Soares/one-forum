package br.one.forum.seeders.factories;

import br.one.forum.security.PasswordCrypt;
import br.one.forum.utils.DataFaker;
import br.one.forum.entities.Profile;
import br.one.forum.entities.User;

import java.util.ArrayList;
import java.util.List;


public class FakeUserFactory {

    public static User getOne() {
        var user = new User();
        var password = new PasswordCrypt().passwordEncoder().encode("12345678");
        user.setEmail(DataFaker.faker().internet().emailAddress());
        user.setPassword(password);
        user.setProfile(new Profile(user, DataFaker.faker().name().firstName()));
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
