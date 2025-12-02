package br.one.forum.services;

import org.springframework.lang.Nullable;

import java.util.Map;

public interface EmailService {
    void sendSimpleMessage(String dest, String subject, String message);
    void sendHtmlMessage(String dest, String subject, String htmlTemplateResource, @Nullable Map<String, Object> params) throws InterruptedException;
}
