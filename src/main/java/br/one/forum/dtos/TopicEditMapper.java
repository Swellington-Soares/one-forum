package br.one.forum.dtos;

import br.one.forum.entities.Topic;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TopicEditMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Topic partialUpdate(TopicEditRequestDto topicEditRequestDto, @MappingTarget Topic topic);
}