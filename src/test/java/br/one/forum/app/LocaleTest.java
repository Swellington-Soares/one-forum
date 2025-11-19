package br.one.forum.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
class LocaleTest {


    private static MessageSource messageSource;

    @BeforeAll
    public static void setup() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("i18n");
        source.setDefaultEncoding("UTF-8");
        messageSource = source;
    }


    @Test
    void testDefaultBundleLocale() {
        String message = messageSource.getMessage("exception.user-not-found", new Object[]{1}, Locale.ENGLISH);
        assertThat(message).isEqualTo("User with id: 1 not found.");

    }

    @Test
    void testPortugueseBundleLocale() {
        String message = messageSource.getMessage("exception.user-not-found", new Object[]{1}, Locale.of("pt", "BR"));
        assertThat(message).isEqualTo("Usuário com ID 1 não existe.");

    }


}
