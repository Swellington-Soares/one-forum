package br.one.forum.exception;

import br.one.forum.infra.I18nUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
class GlobalMvcControllerAdvice {

    private final I18nUtils i18nUtils;

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex, HttpServletRequest req, Locale locale) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", new Exception(i18nUtils.translate(ex.getMessage(), locale)));
        mav.addObject("url", req.getRequestURL());
        mav.setViewName("error");
        return mav;
    }

}
