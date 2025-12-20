package br.one.forum.services;

import br.one.forum.component.EmailQueue;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
@RequiredArgsConstructor
class EmailServiceImp implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final EmailQueue emailQueue;

    @Override
    public void sendSimpleMessage(String dest, String subject, String message) {
        emailQueue.add(() -> internalSendSimpleMessage(dest, subject, message));
    }

    @Override
    public void sendHtmlMessage(String dest, String subject, String htmlTemplateResource, Map<String, Object> params) {
        emailQueue.add(() -> internalSendHtmlMessage(dest, subject, htmlTemplateResource, params));
    }

    void internalSendHtmlMessage(String dest, String subject, String htmlTemplateResource, Map<String, Object> params) {
        try {
            var context = new Context();
            if (params != null) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    context.setVariable(entry.getKey(), entry.getValue());
                }
            }

            var html = templateEngine.process("email/" + htmlTemplateResource, context);
            var mimeMessage = mailSender.createMimeMessage();
            var mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
            mimeMessageHelper.setFrom("no-reply@forum-one.net");
            mimeMessageHelper.setTo(dest);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(html, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
        }
    }

    void internalSendSimpleMessage(String dest, String subject, String message) {
        try {
            var mimeMessage = mailSender.createMimeMessage();
            var mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
            mimeMessageHelper.setTo(dest);
            mimeMessageHelper.setText(message);
            mimeMessageHelper.setSubject(subject);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
        }
    }
}
