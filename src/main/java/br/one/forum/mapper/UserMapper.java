package br.one.forum.mapper;


import br.one.forum.dto.response.UserInfoResponseDto;
import br.one.forum.dto.response.UserProfileResponseDto;
import br.one.forum.entity.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "profileName", source = "user.profile.name")
    @Mapping(target = "profilePhoto", source = "user.profile.photo")
    @Mapping(target = "id", source = "user.id")
    UserInfoResponseDto toUserInfoResponseDto(User user);
    
    @InheritInverseConfiguration(name = "toUserInfoResponseDto")
    @Mapping(target = "topicCreatedCount", expression = "java(user.getTopicCreatedCount())")
    @Mapping(target = "commentsCount", expression = "java(user.getCommentsCount())")
    UserProfileResponseDto toUserProfileInfoResponseDto(User user);
}
