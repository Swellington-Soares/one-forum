package br.one.forum.validation.interfaces;

import br.one.forum.validation.StrongPasswordValidatorImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidatorImpl.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
    String message() default "{validation.strong-password}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
