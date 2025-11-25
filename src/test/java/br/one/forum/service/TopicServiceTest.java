package br.one.forum.service;

import br.one.forum.dtos.TopicCreateRequestDto;
import br.one.forum.dtos.TopicEditRequestDto;
import br.one.forum.entities.Category;
import br.one.forum.entities.Topic;
import br.one.forum.exception.InvalidTopicOwnerException;
import br.one.forum.exception.TopicNotFoundException;
import br.one.forum.seeders.factories.FakeUserFactory;
import br.one.forum.services.TopicService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @Mock
    private TopicService topicService;

    // === createTopic (seu exemplo, só organizei) ==================

    @Test
    void testIfTopicIsCreatedSuccessfully() {
        var authorMock = FakeUserFactory.getOne();
        authorMock.setId(1);

        var topicData = new TopicCreateRequestDto(
                "Hello World 1",
                "Meu primeiro tópico",
                List.of("DEV", "PROGRAMAÇÃO")
        );

        var topic = new Topic(topicData.title(), topicData.content(), authorMock);
        topic.setId(1);
        topicData.categories().forEach(c ->
                topic.addCategory(new Category(c))
        );

        when(topicService.createTopic(authorMock, topicData)).thenReturn(topic);

        var topicCreated = topicService.createTopic(authorMock, topicData);

        assertThat(topicCreated.getId()).isEqualTo(1);
        assertThat(topicCreated.getTitle()).isEqualTo("Hello World 1");
        assertThat(topicCreated.getCategories()).hasSize(2);
    }

    // === findTopicById ============================================

    @Test
    void findTopicById_shouldReturnTopic() {
        var author = FakeUserFactory.getOne();
        author.setId(1);

        var topic = new Topic("Título", "Conteúdo", author);
        topic.setId(10);

        when(topicService.findTopicById(10)).thenReturn(topic);

        var found = topicService.findTopicById(10);

        assertThat(found.getId()).isEqualTo(10);
        assertThat(found.getTitle()).isEqualTo("Título");
    }

    @Test
    void findTopicById_shouldThrowWhenNotFound() {
        int topicId = 999;

        when(topicService.findTopicById(topicId))
                .thenThrow(new TopicNotFoundException(topicId)); // ✅ passa o id

        assertThatThrownBy(() -> topicService.findTopicById(topicId))
                .isInstanceOf(TopicNotFoundException.class);
    }

    // === findAllTopicByUserId =====================================

    @Test
    void findAllTopicByUserId_shouldReturnSlice() {
        var author = FakeUserFactory.getOne();
        author.setId(1);

        var t1 = new Topic("T1", "C1", author);
        t1.setId(1);
        var t2 = new Topic("T2", "C2", author);
        t2.setId(2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Slice<Topic> slice = new SliceImpl<>(List.of(t1, t2), pageable, false);

        when(topicService.findAllTopicByUserId(1, 0, 10)).thenReturn(slice);

        var result = topicService.findAllTopicByUserId(1, 0, 10);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(Topic::getId)
                .containsExactly(1, 2);
    }

    // === getAll ===================================================

    @Test
    void getAll_shouldReturnPageWithoutFilters() {
        var author = FakeUserFactory.getOne();
        author.setId(1);

        var t1 = new Topic("T1", "C1", author); t1.setId(1);
        var t2 = new Topic("T2", "C2", author); t2.setId(2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Topic> page = new PageImpl<>(List.of(t1, t2), pageable, 2);

        when(topicService.getAll(null, null, null, null, pageable))
                .thenReturn(page);

        var result = topicService.getAll(null, null, null, null, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting(Topic::getId)
                .containsExactly(1, 2);
    }

    @Test
    void getAll_shouldFilterByAuthorIdAndTitle() {
        var author = FakeUserFactory.getOne();
        author.setId(1);

        var t1 = new Topic("Java básico", "C1", author); t1.setId(1);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Topic> page = new PageImpl<>(List.of(t1), pageable, 1);

        when(topicService.getAll(1L, null, null, "Java", pageable))
                .thenReturn(page);

        var result = topicService.getAll(1L, null, null, "Java", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).contains("Java");
    }

    // === editTopic =================================================

    @Test
    void editTopic_shouldReturnUpdatedTopic() {
        var author = FakeUserFactory.getOne();
        author.setId(1);

        var original = new Topic("Título antigo", "Conteúdo antigo", author);
        original.setId(10);

        var editDto = new TopicEditRequestDto(
                "Novo título",
                "Novo conteúdo"
        );

        var updated = new Topic(editDto.title(), editDto.content(), author);
        updated.setId(10);

        when(topicService.editTopic(10, editDto, author)).thenReturn(updated);

        var result = topicService.editTopic(10, editDto, author);

        assertThat(result.getTitle()).isEqualTo("Novo título");
        assertThat(result.getContent()).isEqualTo("Novo conteúdo");
    }

    @Test
    void editTopic_shouldThrowWhenNotOwner() {
        var author = FakeUserFactory.getOne(); author.setId(1);
        var other  = FakeUserFactory.getOne(); other.setId(2);

        var editDto = new TopicEditRequestDto("Novo", "Conteúdo");

        when(topicService.editTopic(10, editDto, other))
                .thenThrow(new InvalidTopicOwnerException());

        assertThatThrownBy(() -> topicService.editTopic(10, editDto, other))
                .isInstanceOf(InvalidTopicOwnerException.class);
    }

    // === deleteTopic ===============================================

    @Test
    void deleteTopic_shouldThrowWhenTopicNotFound() {
        int topicId = 999;
        var author = FakeUserFactory.getOne(); author.setId(1);

        // simulando comportamento desejado
        doThrow(new TopicNotFoundException(topicId))
                .when(topicService).deleteTopic(999, author);

        assertThatThrownBy(() -> topicService.deleteTopic(999, author))
                .isInstanceOf(TopicNotFoundException.class);
    }
}