package br.one.forum.mappers;

import br.one.forum.dtos.TopicResponseDto;
import br.one.forum.entities.Topic;
import br.one.forum.entities.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface TopicResponseMapper {

    @Mapping(target = "likes", expression = "java(topic.getLikeCount())")
    @Mapping(target = "likedByCurrentUser", expression = "java(topic.isLikedByUser(user))")
    TopicResponseDto toDto(Topic topic, @Context User user);

    @Mapping(target = "likes", expression = "java(topic.getLikeCount())")
    @Mapping(target = "likedByCurrentUser", expression = "java(topic.isLikedByUser(user))")
    @Mapping(target = "content", ignore = true)
    TopicResponseDto toDtoExcludeContent(Topic topic, @Context User user);
}

