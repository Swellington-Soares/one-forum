package br.one.forum.mapper;


import br.one.forum.dto.response.TopicResponseDto;
import br.one.forum.entity.Topic;
import br.one.forum.entity.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,  componentModel = MappingConstants.ComponentModel.SPRING)
public interface TopicMapper {

    @Mapping(target = "likes", expression = "java(topic.getLikeCount())")
    @Mapping(target = "commentCount", expression = "java(topic.getCommentCount())")
    @Mapping(target = "likedByCurrentUser", expression = "java(topic.isLikedByUser(user))")
    @Mapping(target = "id", source = "topic.id")
    @Mapping(target = "createdAt", source = "topic.createdAt")
    @Mapping(target = "updatedAt", source = "topic.updatedAt")
    @Mapping(target = "categories", expression = "java(topic.getCategoryList())")
    TopicResponseDto toFullResponseDto(Topic topic, User user);

    @InheritConfiguration(name = "toFullResponseDto")
    @Mapping(target = "content", expression = "java(topic.sumarize())")
    TopicResponseDto toResumedResponseDto(Topic topic, User user);
}
