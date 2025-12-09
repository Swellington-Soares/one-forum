package br.one.forum.controllers;

import br.one.forum.dtos.request.UserPasswordChangeRequestDto;
import br.one.forum.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserAuthChangeController {

//    private final UserService userService;
//
//    @GetMapping("/confirm-email/{token}")
//    public String getConfirmEmail(
//            @PathVariable("token") String token,
//            Model model
//    )  {
//        try {
//            userService.confirmEmail(token);
//        } catch (Exception e) {
//            model.addAttribute("status", "error");
//        }
//        return "confirm-info";
//    }
}
