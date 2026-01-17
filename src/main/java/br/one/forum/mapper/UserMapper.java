package br.one.forum.mapper;


import br.one.forum.dto.response.UserInfoResponseDto;
import br.one.forum.dto.response.UserProfileResponseDto;
import br.one.forum.entity.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "profileName", expression = "java(user.getProfile().getName())")
    @Mapping(target = "profilePhoto", expression = "java(user.getProfile().getPhoto())")
    @Mapping(target = "profileBio", expression = "java(user.getProfile().getBio())")
    @Mapping(target = "topicCreatedCount", expression = "java(user.getTopicCreatedCount())")
    @Mapping(target = "commentsCount", expression = "java(user.getCommentsCount())")
    UserProfileResponseDto toUserProfileInfoResponseDto(User user);

    @Mapping(target = "profileName", source = "profile.name")
    @Mapping(target = "profilePhoto", source = "profile.photo")
    @Mapping(target = "profileBio", source = "profile.bio")
    UserInfoResponseDto toUserInfoResponseDto(User user);
}
