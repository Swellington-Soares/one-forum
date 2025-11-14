package br.one.forum.exception;

public class TopicNotFoundException extends RuntimeException {
    public TopicNotFoundException() {
        super("Tópico não encontrado.");
    }
}
