package br.one.forum.validation;

import br.one.forum.dtos.request.UserRegisterRequestDto;
import br.one.forum.validation.interfaces.PasswordMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserRegisterRequestDto> {


    @Override
    public boolean isValid(UserRegisterRequestDto dto, ConstraintValidatorContext context) {
        if (dto.matchPassword() == null || dto.password() == null) return true;
        boolean valid = dto.password().equals(dto.matchPassword());

        if (!valid) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate(
                            context.getDefaultConstraintMessageTemplate()
                    )
                    .addPropertyNode("matchPassword") // 👈 campo alvo
                    .addConstraintViolation();
        }

        return valid;
    }
}
