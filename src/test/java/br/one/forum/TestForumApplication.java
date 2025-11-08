package br.one.forum;

import org.springframework.boot.SpringApplication;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class TestForumApplication {

    static void main(String[] args) {
        SpringApplication.from(ForumApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
