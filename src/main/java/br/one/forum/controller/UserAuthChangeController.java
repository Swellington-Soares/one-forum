package br.one.forum.controller;


import br.one.forum.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserAuthChangeController {
    private final UserService userService;

    @GetMapping(value = "/confirm-account/{token}", produces = "text/html")
    public String activateAccount(
            @PathVariable String token,
            Model model
    ) {
        try {
            userService.confirmEmail(token);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "confirm-info";
    }
}
