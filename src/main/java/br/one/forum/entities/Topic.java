package br.one.forum.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"likedBy", "comments", "author", "categories"})
@Entity
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private String title;

    @NotNull
    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "created_at", nullable = false, updatable = false)
    //@Setter(AccessLevel.NONE)
    //@CreationTimestamp
    //todo: refazer na produção, apenas teste.
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    //@Setter(AccessLevel.NONE)
    //@UpdateTimestamp
    //todo: refazer na produção, apenas teste.
    private Instant updatedAt = Instant.now();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "category_has_topic",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Setter(AccessLevel.NONE)
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "likes",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Setter(AccessLevel.NONE)
    private Set<User> likedBy = new HashSet<>();

    @OneToMany(mappedBy = "topic", orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private Set<Comment> comments = new HashSet<>();


    public Topic(String title, String content, User author, Category category) {
        this.title = title;
        this.content = content;
        this.author = author;
        category.addTopic(this);
        this.categories.add(category);
    }

    public Topic(String title, String content, User author, String category) {
        this(title, content, author, new Category(category));
    }


    public Topic(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = Instant.now();
    }


    public int getLikeCount() {
        return likedBy.size();
    }

    public boolean isLikedByUser(User testUser) {
        return testUser != null && likedBy.stream().anyMatch(u -> Objects.equals(u.getId(), testUser.getId()));
    }

    public void addCategory(String name, Function<String, Category> categoryResolver) {
        if (StringUtils.hasText(name)) {
            var normalized = name.trim().toUpperCase();
            var category = categoryResolver.apply(normalized);
            if (category != null && categories.stream().noneMatch(c -> c.equals(category))) {
                categories.add(category);
            }
        }
    }

    public void addCategory(@NotNull Category category) {
        if (categories.stream().noneMatch(c -> c.getName().equals(category.getName().toUpperCase()))) {
            category.setName(category.getName().toUpperCase());
            categories.add(category);
        }
    }

    public void toggleLike(User likeUser) {
        if (likeUser == null) return;
        if (likeUser.equals(author)) return;

        if (likedBy.contains(likeUser)) {
            likedBy.remove(likeUser);
            likeUser.getLikedTopics().remove(this);
        } else {
            likedBy.add(likeUser);
            likeUser.getLikedTopics().add(this);
        }
    }

}
