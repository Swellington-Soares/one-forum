package br.one.forum.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {

    String message() default "validation.strong-password";

    int min() default 6;

    int max() default 8;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
