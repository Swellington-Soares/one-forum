package br.one.forum.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {

    String message() default "Senha fraca: deve conter letras maiúsculas, minúsculas, números e símbolos";
    int min() default 6;
    int max() default 8;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
