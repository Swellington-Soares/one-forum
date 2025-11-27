package br.one.forum.mappers;

import br.one.forum.dtos.UserAuthResponseDto;
import br.one.forum.dtos.UserPublicResponseDto;
import br.one.forum.entities.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mapping(source = "profileBio", target = "profile.bio")
    @Mapping(source = "profilePhoto", target = "profile.photo")
    @Mapping(source = "profileName", target = "profile.name")
    User toEntity(UserPublicResponseDto usePublic);

    @InheritInverseConfiguration(name = "toEntity")
    UserPublicResponseDto toPublicDto(User user);

    @InheritInverseConfiguration(name = "toEntity")
    UserAuthResponseDto toAuthUserDto(User user);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserPublicResponseDto usePublic, @MappingTarget User user);
}