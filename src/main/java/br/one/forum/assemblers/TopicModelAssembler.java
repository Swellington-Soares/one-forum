package br.one.forum.assemblers;


import br.one.forum.controllers.TopicController;
import br.one.forum.dtos.TopicResponseDto;
import br.one.forum.mappers.TopicResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class TopicModelAssembler
        implements RepresentationModelAssembler<TopicResponseDto, EntityModel<TopicResponseDto>> {

    private final TopicResponseMapper responseMapper;
    @Override
    @NonNull
    public EntityModel<TopicResponseDto> toModel(@NonNull TopicResponseDto dto) {
        return EntityModel.of(
                dto,
                linkTo(methodOn(TopicController.class).getTopic(dto.id())).withSelfRel()
        );
    }


}
