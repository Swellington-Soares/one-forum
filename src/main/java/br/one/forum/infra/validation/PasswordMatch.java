package br.one.forum.infra.validation;

import br.one.forum.infra.validation.impl.PasswordMatchValidatorImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchValidatorImpl.class)
public @interface PasswordMatch {
    String message() default "{exception.password-not-match}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String passwordField();
    String confirmPasswordField();
}
