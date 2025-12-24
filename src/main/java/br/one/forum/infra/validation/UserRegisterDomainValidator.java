package br.one.forum.infra.validation;

import br.one.forum.dto.request.UserRegisterRequestDto;

public interface UserRegisterDomainValidator {
    void validate(UserRegisterRequestDto dto);
}
