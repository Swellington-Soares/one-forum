package br.one.forum.mappers;

import br.one.forum.dtos.CommentResponseDto;
import br.one.forum.dtos.CreateCommentDto;
import br.one.forum.entities.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface CommentMapper {

    Comment toEntity(CreateCommentDto dto);

    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "userId", source = "user.id")
    CommentResponseDto toDto(Comment comment);
}
