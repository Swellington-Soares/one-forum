package br.one.forum.infra.validation;


import br.one.forum.infra.validation.impl.StrongPasswordValidatorImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = StrongPasswordValidatorImpl.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
    String message() default "{validation.strong-password}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
