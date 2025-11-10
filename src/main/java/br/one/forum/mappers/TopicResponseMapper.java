package br.one.forum.mappers;

import br.one.forum.dtos.TopicResponseDto;
import br.one.forum.entities.Topic;
import br.one.forum.entities.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TopicResponseMapper {
    Topic toEntity(TopicResponseDto topicResponseDto);

    @Mapping(target = "likes",  expression = "java(topic.getLikeCount())")
    @Mapping(target = "likedByCurrentUser",  expression = "java(topic.isLikedByUser(user))")
    TopicResponseDto toDto(Topic topic, @Context User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Topic partialUpdate(TopicResponseDto topicResponseDto, @MappingTarget Topic topic);
}