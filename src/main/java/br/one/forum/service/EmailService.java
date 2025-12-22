package br.one.forum.service;


import br.one.forum.infra.worker.email.ProcessEmailQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final ProcessEmailQueue emailQueue;

    private void _internalSendHtmlMessage(
            String dest,
            String subject,
            String htmlTemplateResource,
            Map<String, Object> params
    ) {
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
        } catch (Exception ignored) {
        }
    }

    private void _internalSendTextMessage(String dest, String subject, String message) {
        try {
            var mimeMessage = mailSender.createMimeMessage();
            var mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
            mimeMessageHelper.setFrom("no-reply@forum-one.net");
            mimeMessageHelper.setTo(dest);
            mimeMessageHelper.setText(message);
            mimeMessageHelper.setSubject(subject);
            mailSender.send(mimeMessage);
        } catch (Exception ignored) {
        }
    }


    public void SendSimpleMessage(String dest, String subject, String message) {
        emailQueue.addJob(() -> _internalSendTextMessage(dest, subject, message));
    }

    public void sendHtmlMessage(String dest, String subject, String htmlTemplateResource, Map<String, Object> params) {
        emailQueue.addJob(() ->
                _internalSendHtmlMessage(
                        dest,
                        subject,
                        htmlTemplateResource,
                        params
                )
        );
    }
}
