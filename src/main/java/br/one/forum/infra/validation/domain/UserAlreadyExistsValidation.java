package br.one.forum.infra.validation.domain;

import br.one.forum.dto.request.UserRegisterRequestDto;
import br.one.forum.exception.api.UserAlreadyRegisteredException;
import br.one.forum.infra.validation.UserRegisterDomainValidator;
import br.one.forum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAlreadyExistsValidation implements UserRegisterDomainValidator {

    private final UserRepository userRepository;

    @Override
    public void validate(UserRegisterRequestDto dto) {
        if ( userRepository.existsByEmailIgnoreCase(dto.email()))
            throw new UserAlreadyRegisteredException();
    }
}
