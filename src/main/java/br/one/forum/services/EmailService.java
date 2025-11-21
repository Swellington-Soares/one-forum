package br.one.forum.services;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

@Service
class EmailService {

    public void SendConfirmAccountEmail(@NotBlank @Email String email, String emailToken) {

    }
}
