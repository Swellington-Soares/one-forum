package br.one.forum.factories;

import br.one.forum.DataFaker;
import br.one.forum.entities.Topic;
import br.one.forum.entities.User;

import java.util.ArrayList;
import java.util.List;

public class FakeTopicFactory {

    public Topic getOne(List<User> owners) {
        if (owners == null || owners.isEmpty()) {
            throw new IllegalArgumentException("Owner list is null or empty");
        }
        var topic = new Topic();
        topic.setUser(owners.get(DataFaker.faker().number().numberBetween(0, owners.size() - 1)));
        topic.setContent(DataFaker.faker().lorem().paragraph(1));
        topic.setTitle(DataFaker.faker().book().title());
        topic.addCategory(FakeCategoryFactory.get());
        return topic;
    }

    public List<Topic> getMore(int max, List<User> owners) {
        var list = new ArrayList<Topic>();
        for (int i = 1; i <= max; i++) {
            list.add(getOne(owners));
        }
        return list;
    }
}
