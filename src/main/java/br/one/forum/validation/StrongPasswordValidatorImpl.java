package br.one.forum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidatorImpl implements ConstraintValidator<StrongPassword, String> {

    private int min = 6;
    private int max = 8;

    @Override
    public void initialize(StrongPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (password == null) return false;

        String pattern = String.format(
                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%%*?&._-])[A-Za-z\\d@$!%%*?&._-]{%d,%d}$",
                min, max
        );

        return password.matches(pattern);
    }
}
