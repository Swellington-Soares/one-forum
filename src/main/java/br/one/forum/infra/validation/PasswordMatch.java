package br.one.forum.infra.validation;

import br.one.forum.infra.validation.impl.PasswordMatchValidatorImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchValidatorImpl.class)
public @interface PasswordMatch {
    String message() default "{exception.password-not-match}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
