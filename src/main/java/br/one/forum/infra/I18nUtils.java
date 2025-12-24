package br.one.forum.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class I18nUtils {

    private final MessageSource messageSource;

    public String translate(String messageKey, Locale locale, Object... args) {
        var pattern = "^\\{.+}$";

        if (messageKey != null && messageKey.matches(pattern)) {
            var message = messageKey.replaceFirst("\\{", "").replaceFirst("\\}(?!.*})", "");
            return messageSource.getMessage(message, args, locale);
        }
        return messageKey;
    }
}
