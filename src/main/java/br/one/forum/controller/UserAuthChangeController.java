package br.one.forum.controller;


import br.one.forum.dto.request.UserPasswordUpdateRequestDto;
import br.one.forum.exception.api.PasswordSameAsOldException;
import br.one.forum.infra.I18nUtils;
import br.one.forum.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.web.exchanges.HttpExchange;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserAuthChangeController {

    private final UserService userService;
    private final I18nUtils i18nUtils;

    @GetMapping(value = "/confirm-account/{token}", produces = "text/html")
    public String activateAccount(
            @PathVariable String token,
            Model model
    ) {
        try {
            userService.confirmEmail(token);
        } catch (Exception e) {
            model.addAttribute("error", i18nUtils.translate(e.getMessage(), LocaleContextHolder.getLocale()));
        }
        return "confirm-info";
    }

    /**
     * Mostra a tela para colocar o e-mail, para assim iniciar o procedimento de recuperação de senha
     *
     */
    @GetMapping("/request-password-change")
    public String requestPasswordChange(
            Model model
    ) {
        model.addAttribute("email", "");
        return "request-password-change";
    }

    @PostMapping("/request-password-change")
    public String passwordUserChange(
            @ModelAttribute("email") String email,
            Model model
    ) {
        userService.sendPasswordChangeRequest(email);
        model.addAttribute("info", "Solicitação concluída se o e-mail existir na nossa base," +
                " você vai receber uma mensagem.");
        model.addAttribute("email","");
        return "request-password-change";
    }

    @GetMapping("/change-password/{token}")
    public String changePasswordPage(
            @PathVariable String token,
            Model model
    ) {
        try {
            userService.validatePasswordToken(token);
            model.addAttribute("token", token);
            model.addAttribute("passwordForm", new UserPasswordUpdateRequestDto("", ""));
        } catch (Exception e) {
            model.addAttribute("error", i18nUtils.translate(e.getMessage(), LocaleContextHolder.getLocale()));
        }
        return "change-password";
    }

    @PostMapping("/change-password/{token}")
    public String changePassword(
            @PathVariable String token,
            @Valid @ModelAttribute("passwordForm") UserPasswordUpdateRequestDto form,
            BindingResult bindingResult,
            Model model) {
        model.addAttribute("token", token);

        if  (bindingResult.hasErrors()) {
            return "change-password";
        }

        try {
            userService.checkAndUpdateUserPassword(token, form);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", i18nUtils.translate(e.getMessage(), LocaleContextHolder.getLocale()));
            if (e instanceof PasswordSameAsOldException) {
                model.addAttribute("trybutton", true);
            }
        }
        return "change-password";
    }
}
