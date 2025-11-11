package br.one.forum.seeders;

import br.one.forum.entities.Category;
import br.one.forum.entities.Topic;
import br.one.forum.entities.User;
import br.one.forum.repositories.CategoryRepository;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.repositories.UserRepository;
import br.one.forum.seeders.factories.FakeCategoryFactory;
import br.one.forum.seeders.factories.FakeTopicFactory;
import br.one.forum.seeders.factories.FakeUserFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


@Component
public final class DatabaseSeeder {

    @Bean
    public CommandLineRunner initDatabase(
            final UserRepository userRepository,
            final TopicRepository topicRepository,
            final CategoryRepository categoryRepository) {
        return (args) -> {
            if (Arrays.stream(args).noneMatch("--add-seed"::equals)) return;
            IO.println("ATUALIZAÇÃO DOS SEEDERS NO BANCO DE DADOS");
            List<User> users = userRepository.saveAll(FakeUserFactory.getMore(50));
            List<Category> categories = categoryRepository.saveAll(FakeCategoryFactory.getAll());
            List<Topic> topics = FakeTopicFactory.getMore(50, users);
            topics.forEach(topic -> {
                topic.addCategory(categories.get(new Random().nextInt(0, categories.size() - 1)));
            });
            topicRepository.saveAll(topics);
            IO.println("SEEDS ATUALIZADOS COM SUCESSO.");

        };
    }
}
