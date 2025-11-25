package br.one.forum.mappers;

import br.one.forum.dtos.UserCommentResponseDto;
import br.one.forum.entities.Comment;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserCommentMapper {
    @Mapping(source = "topicCreatedAt", target = "topic.createdAt")
    @Mapping(source = "topicTitle", target = "topic.title")
    @Mapping(source = "topicId", target = "topic.id")
    Comment toEntity(UserCommentResponseDto userCommentResponseDto);

    @InheritInverseConfiguration(name = "toEntity")
    UserCommentResponseDto toDto(Comment comment);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Comment partialUpdate(UserCommentResponseDto userCommentResponseDto, @MappingTarget Comment comment);
}