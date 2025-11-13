package br.one.forum.exception;

public class FileUploadException extends RuntimeException {
    public FileUploadException() {
        super("Não foi possível fazer o Upload do arquivo.");
    }
}
