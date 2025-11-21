package br.one.forum.mappers;

import br.one.forum.dtos.CommentResponseDto;
import br.one.forum.entities.Comment;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(source = "userProfilePhoto", target = "user.profile.photo")
    @Mapping(source = "userProfileName", target = "user.profile.name")
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "topicId", target = "topic.id")
    Comment toEntity(CommentResponseDto commentResponseDto);

    @InheritInverseConfiguration(name = "toEntity")
    CommentResponseDto toDto(Comment comment);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Comment partialUpdate(CommentResponseDto commentResponseDto, @MappingTarget Comment comment);
}