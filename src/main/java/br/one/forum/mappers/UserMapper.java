package br.one.forum.mappers;

import br.one.forum.dtos.UserResponseDto;
import br.one.forum.entities.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mapping(source = "profileBio", target = "profile.bio")
    @Mapping(source = "profilePhoto", target = "profile.photo")
    @Mapping(source = "profileName", target = "profile.name")
    User toEntity(UserResponseDto userResponseDto);

    @InheritInverseConfiguration(name = "toEntity")
    UserResponseDto toDto(User user);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserResponseDto userResponseDto, @MappingTarget User user);
}