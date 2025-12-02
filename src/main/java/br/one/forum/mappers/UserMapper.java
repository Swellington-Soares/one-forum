package br.one.forum.mappers;

import br.one.forum.dtos.response.UserProfileResponseDto;
import br.one.forum.entities.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mapping(source = "profileBio", target = "profile.bio")
    @Mapping(source = "profilePhoto", target = "profile.photo")
    @Mapping(source = "profileName", target = "profile.name")
    User toEntity(UserProfileResponseDto userProfileResponseDto);

    @InheritInverseConfiguration(name = "toEntity")
    @Mapping(target = "topicCreatedCount", expression = "java(user.getTopicCreatedCount())")
    @Mapping(target = "commentsCount", expression = "java(user.getCommentsCount())")
    UserProfileResponseDto toDto(User user);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserProfileResponseDto userProfileResponseDto, @MappingTarget User user);
}