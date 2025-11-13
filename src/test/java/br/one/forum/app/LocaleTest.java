package br.one.forum.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
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
    void testDefaultBundleLocale(){
        String message = messageSource.getMessage("exception.user_not_found",  null, Locale.ENGLISH);
        assertThat(message).isEqualTo("User not found");

    }

    @Test
    void testPortugueseBundleLocale(){
        Locale locale = Locale.of("pt", "BR");
        String message = messageSource.getMessage("exception.user_not_found",  null, locale);
        assertThat(message).isEqualTo("Usuário não encontrado");

    }


}
