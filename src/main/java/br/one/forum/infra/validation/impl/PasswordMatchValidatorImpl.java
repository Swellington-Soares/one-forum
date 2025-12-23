package br.one.forum.infra.validation.impl;


import br.one.forum.dto.request.UserRegisterRequestDto;
import br.one.forum.infra.validation.PasswordMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class PasswordMatchValidatorImpl implements ConstraintValidator<PasswordMatch, Object> {

    private String passwordField;
    private String confirmPasswordField;


    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        this.confirmPasswordField = constraintAnnotation.confirmPasswordField();
        this.passwordField = constraintAnnotation.passwordField();
    }

    @Override
    public boolean isValid(Object value,
                           ConstraintValidatorContext context) {
        if (value == null) return true;

        var wrapper = new BeanWrapperImpl(value);

        Object password = wrapper.getPropertyValue(passwordField);
        Object confirmPassword = wrapper.getPropertyValue(confirmPasswordField);

        if (password == null || confirmPassword == null) return true;

        var valid = password.equals(confirmPassword);

        if (!valid) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate(
                            context.getDefaultConstraintMessageTemplate()
                    )
                    .addPropertyNode(confirmPasswordField)
                    .addConstraintViolation();
        }

        return valid;
    }
}
