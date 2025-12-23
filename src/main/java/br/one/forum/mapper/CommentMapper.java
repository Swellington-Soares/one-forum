package br.one.forum.mapper;


import br.one.forum.dto.response.CommentResponseDto;
import br.one.forum.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,  componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "userId", source = "author.id")
    @Mapping(target = "userProfileName", source = "author.profile.name")
    @Mapping(target = "userProfilePhoto", source = "author.profile.photo")
    CommentResponseDto toResponseDto(Comment comment);

}
