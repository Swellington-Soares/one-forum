package br.one.forum.seeders.factories;

import br.one.forum.entities.Topic;
import br.one.forum.entities.User;
import br.one.forum.utils.DataFaker;

import java.util.ArrayList;
import java.util.List;

public class FakeTopicFactory {

    public static Topic getOne(List<User> owners) {
        if (owners == null || owners.isEmpty()) {
            throw new IllegalArgumentException("Owner list is null or empty");
        }
        var topic = new Topic();
        topic.setUser(owners.get(DataFaker.faker().number().numberBetween(0, owners.size() - 1)));
        topic.setContent(DataFaker.faker().lorem().paragraph(1));
        topic.setTitle(DataFaker.faker().book().title());
        return topic;
    }

    public static List<Topic> getMore(int max, List<User> owners) {
        var list = new ArrayList<Topic>();
        for (int i = 1; i <= max; i++) {
            list.add(getOne(owners));
        }
        return list;
    }
}
