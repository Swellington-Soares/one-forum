package br.one.forum.mapper;


import br.one.forum.dto.response.CommentResponseDto;
import br.one.forum.entity.Comment;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,  componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(source = "userProfilePhoto", target = "author.profile.photo")
    @Mapping(source = "userProfileName", target = "author.profile.name")
    @Mapping(source = "userId", target = "author.id")
    @Mapping(source = "topicId", target = "topic.id")
    Comment toEntity(CommentResponseDto commentResponseDto);

    @InheritInverseConfiguration(name = "toEntity")
    CommentResponseDto toResponseDto(Comment comment);

}
