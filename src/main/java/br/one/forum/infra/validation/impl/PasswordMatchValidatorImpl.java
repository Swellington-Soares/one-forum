package br.one.forum.infra.validation.impl;


import br.one.forum.dto.request.UserRegisterRequestDto;
import br.one.forum.infra.validation.PasswordMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidatorImpl implements ConstraintValidator<PasswordMatch, UserRegisterRequestDto> {

    @Override
    public boolean isValid(UserRegisterRequestDto dto,
                           ConstraintValidatorContext context) {
        if (dto.password() == null || dto.matchPassword() == null) return true;

        var valid = dto.password().equals(dto.matchPassword());

        if (!valid) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate(
                            context.getDefaultConstraintMessageTemplate()
                    )
                    .addPropertyNode("matchPassword")
                    .addConstraintViolation();
        }

        return valid;
    }
}
