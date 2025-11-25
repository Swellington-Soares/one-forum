package br.one.forum.service;

import br.one.forum.TestcontainersConfiguration;
import br.one.forum.dtos.TopicCreateRequestDto;
import br.one.forum.dtos.TopicEditRequestDto;
import br.one.forum.entities.Topic;
import br.one.forum.entities.User;
import br.one.forum.exception.InvalidTopicOwnerException;
import br.one.forum.exception.TopicNotFoundException;
import br.one.forum.repositories.TopicRepository;
import br.one.forum.repositories.UserRepository;
import br.one.forum.seeders.factories.FakeTopicFactory;
import br.one.forum.seeders.factories.FakeUserFactory;
import br.one.forum.services.TopicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Transactional
class TopicServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TopicService topicService;

    private User author;

    @BeforeEach
    void setup() {
        topicRepository.deleteAll();
        userRepository.deleteAll();
        author = userRepository.save(FakeUserFactory.getOne());
    }

    // =====================================================
    // Helper – cria Topic “na mão” (sem categorias)
    // =====================================================
    private Topic createTestTopic(User author, String titleSuffix) {
        Topic topic = new Topic();
        topic.setAuthor(author);
        topic.setTitle("Test Topic " + titleSuffix);
        topic.setContent("Test content " + titleSuffix);
        topic.setCreatedAt(Instant.now());
        topic.setUpdatedAt(Instant.now());
        return topicRepository.save(topic);
    }

    private Topic createTestTopic(User author) {
        return createTestTopic(author, String.valueOf(System.nanoTime()));
    }

    // =====================================================
    // toggleLike()
    // =====================================================

    @Test
    void toggleLike_shouldAddLike() {
        Topic topic = createTestTopic(author);
        User user2 = userRepository.save(FakeUserFactory.getOne());

        topicService.toggleLike(topic, user2);

        assertThat(topic.getLikeCount()).isEqualTo(1);
    }

    @Test
    void toggleLike_shouldRemoveLikeWhenAlreadyExists() {
        Topic topic = createTestTopic(author);
        User user2 = userRepository.save(FakeUserFactory.getOne());

        topicService.toggleLike(topic, user2);
        assertThat(topic.getLikeCount()).isEqualTo(1);

        topicService.toggleLike(topic, user2);
        assertThat(topic.getLikeCount()).isEqualTo(0);
    }

    @Test
    void toggleLike_shouldAllowMultipleUsersLiking() {
        Topic topic = createTestTopic(author);
        User user2 = userRepository.save(FakeUserFactory.getOne());
        User user3 = userRepository.save(FakeUserFactory.getOne());

        topicService.toggleLike(topic, user2);
        topicService.toggleLike(topic, user3);

        assertThat(topic.getLikeCount()).isEqualTo(2);
    }

    // =====================================================
    // findTopicById()
    // =====================================================

    @Test
    void findTopicById_shouldReturnExistingTopic() {
        Topic topic = createTestTopic(author);

        Topic found = topicService.findTopicById(topic.getId());

        assertThat(found.getId()).isEqualTo(topic.getId());
        assertThat(found.getTitle()).isEqualTo(topic.getTitle());
    }

    @Test
    void findTopicById_shouldThrowWhenNotFound() {
        assertThatThrownBy(() -> topicService.findTopicById(999_999))
                .isInstanceOf(TopicNotFoundException.class);
    }

    // =====================================================
    // findAllTopicByUserId()
    // =====================================================

    @Test
    void findAllTopicByUserId_shouldReturnUserTopics() {
        createTestTopic(author);
        createTestTopic(author);

        User otherUser = userRepository.save(FakeUserFactory.getOne());
        createTestTopic(otherUser);

        var slice = topicService.findAllTopicByUserId(author.getId(), 0, 10);

        assertThat(slice.getContent())
                .hasSize(2)
                .allMatch(t -> t.getAuthor().getId().equals(author.getId()));
    }

    @Test
    void findAllTopicByUserId_shouldReturnEmptyWhenUserHasNoTopics() {
        User otherUser = userRepository.save(FakeUserFactory.getOne());
        createTestTopic(otherUser);

        var slice = topicService.findAllTopicByUserId(author.getId(), 0, 10);

        assertThat(slice.getContent()).isEmpty();
    }

    @Test
    void findAllTopicByUserId_shouldOrderByNewestFirst() throws InterruptedException {
        Topic older = createTestTopic(author, "older");
        Thread.sleep(5); // garante timestamp diferente
        Topic newer = createTestTopic(author, "newer");

        var slice = topicService.findAllTopicByUserId(author.getId(), 0, 10);

        assertThat(slice.getContent())
                .extracting(Topic::getId)
                .first()
                .isEqualTo(newer.getId());
    }

    @Test
    void findAllTopicByUserId_shouldPaginateCorrectly() {
        createTestTopic(author, "1");
        createTestTopic(author, "2");
        createTestTopic(author, "3");

        var page0 = topicService.findAllTopicByUserId(author.getId(), 0, 2);
        var page1 = topicService.findAllTopicByUserId(author.getId(), 1, 2);

        assertThat(page0.getNumberOfElements()).isEqualTo(2);
        assertThat(page1.getNumberOfElements()).isEqualTo(1);

        // IDs não se repetem entre as páginas
        assertThat(page0.getContent())
                .extracting(Topic::getId)
                .doesNotContainAnyElementsOf(
                        page1.getContent().stream().map(Topic::getId).toList()
                );
    }

    // =====================================================
    // createTopic()
    // =====================================================

    @Test
    void createTopic_shouldCreateWithoutCategories() {
        TopicCreateRequestDto dto =
                new TopicCreateRequestDto("Título sem categoria", "Conteúdo", List.of());

        Topic topic = topicService.createTopic(author, dto);

        assertThat(topic.getId()).isNotNull();
        assertThat(topic.getTitle()).isEqualTo(dto.title());
        assertThat(topic.getCategories()).isEmpty();
    }

    @Test
    void createTopic_shouldCreateWithCategories() {
        TopicCreateRequestDto dto =
                new TopicCreateRequestDto("Título com categoria", "Conteúdo",
                        List.of("Java", "Spring"));

        Topic topic = topicService.createTopic(author, dto);

        assertThat(topic.getCategories())
                .extracting("name")
                .containsExactlyInAnyOrder("Java", "Spring");
    }

    @Test
    void createTopic_shouldReuseExistingCategories() {
        TopicCreateRequestDto dto1 =
                new TopicCreateRequestDto("T1", "Conteúdo 1", List.of("Java"));
        Topic t1 = topicService.createTopic(author, dto1);

        TopicCreateRequestDto dto2 =
                new TopicCreateRequestDto("T2", "Conteúdo 2", List.of("Java"));
        Topic t2 = topicService.createTopic(author, dto2);

        assertThat(t1.getCategories()).hasSize(1);
        assertThat(t2.getCategories()).hasSize(1);

        var c1 = t1.getCategories().iterator().next();
        var c2 = t2.getCategories().iterator().next();

        assertThat(c1.getId()).isEqualTo(c2.getId()); // mesma Category no banco
    }

    @Test
    void createTopic_shouldThrowWhenUserIsNull() {
        TopicCreateRequestDto dto =
                new TopicCreateRequestDto("Qualquer", "Conteúdo", List.of());

        assertThatThrownBy(() -> topicService.createTopic(null, dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // =====================================================
    // getAll()
    // =====================================================

    @Test
    void getAll_shouldReturnAllWithoutFilters() {
        User another = userRepository.save(FakeUserFactory.getOne());

        createTestTopic(author, "a1");
        createTestTopic(author, "a2");
        createTestTopic(another, "b1");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Topic> page = topicService.getAll(null, null, null, null, pageable);

        assertThat(page.getTotalElements()).isEqualTo(3);
    }

    @Test
    void getAll_shouldFilterByAuthorId() {
        User another = userRepository.save(FakeUserFactory.getOne());

        createTestTopic(author, "a1");
        createTestTopic(author, "a2");
        createTestTopic(another, "b1");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Topic> page = topicService.getAll(author.getId().longValue(), null, null, null, pageable);

        assertThat(page.getContent())
                .hasSize(2)
                .allMatch(t -> t.getAuthor().getId().equals(author.getId()));
    }

    @Test
    void getAll_shouldFilterByCategoryId() {
        // cria dois tópicos com categoria "Java"
        TopicCreateRequestDto dtoJava1 =
                new TopicCreateRequestDto("Java 1", "Conteúdo", List.of("Java"));
        TopicCreateRequestDto dtoJava2 =
                new TopicCreateRequestDto("Java 2", "Conteúdo", List.of("Java"));

        Topic java1 = topicService.createTopic(author, dtoJava1);
        Topic java2 = topicService.createTopic(author, dtoJava2);

        // cria um tópico com outra categoria
        TopicCreateRequestDto dtoSpring =
                new TopicCreateRequestDto("Spring 1", "Conteúdo", List.of("Spring"));
        topicService.createTopic(author, dtoSpring);

        Long javaCategoryId = java1.getCategories().iterator().next().getId().longValue();

        Pageable pageable = PageRequest.of(0, 10);

        Page<Topic> page = topicService.getAll(null, null, javaCategoryId, null, pageable);

        assertThat(page.getContent())
                .extracting(Topic::getId)
                .containsExactlyInAnyOrder(java1.getId(), java2.getId());
    }

    @Test
    void getAll_shouldFilterByTitle() {
        createTestTopic(author, "Backend com Java");
        createTestTopic(author, "Frontend com React");
        createTestTopic(author, "Java avançado");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Topic> page = topicService.getAll(null, null, null, "Java", pageable);

        assertThat(page.getContent())
                .extracting(Topic::getTitle)
                .allSatisfy(title ->
                        assertThat(title.toLowerCase()).contains("java")
                );
    }

    @Test
    void getAll_shouldOrderByMostLikedWhenRequested() {
        Topic lessLiked = createTestTopic(author, "less");
        Topic moreLiked = createTestTopic(author, "more");

        User u1 = userRepository.save(FakeUserFactory.getOne());
        User u2 = userRepository.save(FakeUserFactory.getOne());

        topicService.toggleLike(moreLiked, u1);
        topicService.toggleLike(moreLiked, u2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Topic> page = topicService.getAll(null, true, null, null, pageable);

        assertThat(page.getContent())
                .extracting(Topic::getId)
                .first()
                .isEqualTo(moreLiked.getId());
    }

    @Test
    void getAll_shouldCombineFilters() {
        // author 1 com Java/React
        TopicCreateRequestDto dto1 =
                new TopicCreateRequestDto("Java básico", "Conteúdo", List.of("Java"));
        TopicCreateRequestDto dto2 =
                new TopicCreateRequestDto("React básico", "Conteúdo", List.of("React"));

        Topic t1 = topicService.createTopic(author, dto1);
        topicService.createTopic(author, dto2);

        // author 2 com Java
        User another = userRepository.save(FakeUserFactory.getOne());
        TopicCreateRequestDto dto3 =
                new TopicCreateRequestDto("Java avançado", "Conteúdo", List.of("Java"));
        topicService.createTopic(another, dto3);

        Long javaCategoryId = t1.getCategories().iterator().next().getId().longValue();

        Pageable pageable = PageRequest.of(0, 10);

        Page<Topic> page = topicService.getAll(
                author.getId().longValue(),  // filtro author
                null,
                javaCategoryId,              // filtro categoria
                "Java",                      // filtro título
                pageable
        );

        assertThat(page.getContent())
                .hasSize(1)
                .allMatch(t -> t.getAuthor().getId().equals(author.getId()))
                .allMatch(t -> t.getTitle().toLowerCase().contains("java"))
                .allSatisfy(t -> {
                    Set<String> names = t.getCategories().stream().map(c -> c.getName()).collect(java.util.stream.Collectors.toSet());
                    assertThat(names).contains("Java");
                });
    }

    @Test
    void getAll_shouldPaginateCorrectly() {
        for (int i = 0; i < 5; i++) {
            createTestTopic(author, "t" + i);
        }

        Pageable page0Req = PageRequest.of(0, 2);
        Pageable page1Req = PageRequest.of(1, 2);
        Pageable page2Req = PageRequest.of(2, 2);

        Page<Topic> p0 = topicService.getAll(null, null, null, null, page0Req);
        Page<Topic> p1 = topicService.getAll(null, null, null, null, page1Req);
        Page<Topic> p2 = topicService.getAll(null, null, null, null, page2Req);

        assertThat(p0.getNumberOfElements()).isEqualTo(2);
        assertThat(p1.getNumberOfElements()).isEqualTo(2);
        assertThat(p2.getNumberOfElements()).isEqualTo(1);

        // IDs não se repetem entre páginas
        var allIds = p0.getContent().stream().map(Topic::getId).toList();
        allIds = new java.util.ArrayList<>(allIds);
        allIds.addAll(p1.getContent().stream().map(Topic::getId).toList());
        allIds.addAll(p2.getContent().stream().map(Topic::getId).toList());

        assertThat(allIds).doesNotHaveDuplicates();
    }

    // =====================================================
    // editTopic()
    // =====================================================

    @Test
    void editTopic_shouldUpdateTopic() {
        Topic topic = createTestTopic(author);

        TopicEditRequestDto dto =
                new TopicEditRequestDto("Novo título", "Novo conteúdo");

        Topic updated = topicService.editTopic(topic.getId(), dto, author);

        assertThat(updated.getTitle()).isEqualTo("Novo título");
        assertThat(updated.getContent()).isEqualTo("Novo conteúdo");
    }

    @Test
    void editTopic_shouldThrowWhenNotOwner() {
        Topic topic = createTestTopic(author);
        User otherUser = userRepository.save(FakeUserFactory.getOne());

        TopicEditRequestDto dto =
                new TopicEditRequestDto("Tentativa", "Edição");

        assertThatThrownBy(() -> topicService.editTopic(topic.getId(), dto, otherUser))
                .isInstanceOf(InvalidTopicOwnerException.class);
    }

    @Test
    void editTopic_shouldThrowWhenTopicDoesNotExist() {
        TopicEditRequestDto dto =
                new TopicEditRequestDto("Qualquer", "Conteúdo");

        assertThatThrownBy(() -> topicService.editTopic(999_999, dto, author))
                .isInstanceOf(TopicNotFoundException.class);
    }

    @Test
    void editTopic_shouldThrowWhenUserIsNull() {
        Topic topic = createTestTopic(author);

        TopicEditRequestDto dto =
                new TopicEditRequestDto("Qualquer", "Conteúdo");

        assertThatThrownBy(() -> topicService.editTopic(topic.getId(), dto, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void editTopic_shouldDoPartialUpdateWhenSupported() {
        Topic topic = createTestTopic(author);

        // supondo que se vier null, o campo não é atualizado
        TopicEditRequestDto dto =
                new TopicEditRequestDto("Novo título", null);

        Topic updated = topicService.editTopic(topic.getId(), dto, author);

        assertThat(updated.getTitle()).isEqualTo("Novo título");
        assertThat(updated.getContent()).isEqualTo(topic.getContent());
    }

    // =====================================================
    // deleteTopic()
    // =====================================================

    @Test
    void deleteTopic_shouldDeleteForOwner() {
        Topic topic = createTestTopic(author);

        topicService.deleteTopic(topic.getId(), author);

        assertThat(topicRepository.existsById(topic.getId())).isFalse();
    }

    @Test
    void deleteTopic_shouldNotDeleteWhenNotOwner() {
        Topic topic = createTestTopic(author);
        User otherUser = userRepository.save(FakeUserFactory.getOne());

        topicService.deleteTopic(topic.getId(), otherUser);

        // método usa deleteTopicByIdAndAuthorId, então não deve deletar
        assertThat(topicRepository.existsById(topic.getId())).isTrue();
    }
}
