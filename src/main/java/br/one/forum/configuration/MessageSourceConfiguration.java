package br.one.forum.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
class MessageSourceConfiguration {


    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(true);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

}
