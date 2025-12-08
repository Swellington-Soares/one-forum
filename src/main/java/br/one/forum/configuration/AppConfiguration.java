package br.one.forum.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
class AppConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder ->
                builder.modules(new JavaTimeModule())
                        .simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .setSerializationInclusion(
                        JsonInclude.Include.NON_NULL
                ).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    @Bean("emailExecutor")
    public Executor emailExecutor() {
        var threadPoolExecutor = new ThreadPoolTaskExecutor();
        threadPoolExecutor.setCorePoolSize(4);
        threadPoolExecutor.setMaxPoolSize(4);
        threadPoolExecutor.setThreadNamePrefix("email-");
        threadPoolExecutor.initialize();
        return threadPoolExecutor;
    }

}
