package br.one.forum.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "likedBy", "comments", "author", "categories" })
@Entity
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at")
    @Setter(AccessLevel.NONE)
    @UpdateTimestamp
    private Instant updatedAt;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
    @JoinTable(name = "category_has_topic", joinColumns = @JoinColumn(name = "topic_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    @Setter(AccessLevel.NONE)
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "likes", joinColumns = @JoinColumn(name = "topic_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private Set<User> likedBy = new HashSet<>();

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    @JsonIgnore
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
        if (likeUser == null)
            return;
        if (likeUser.equals(author))
            return;

        if (likedBy.stream().anyMatch(u -> u.getId().equals(likeUser.getId()))) {
            likedBy.removeIf(l -> l.getId().equals(likeUser.getId()));
            likeUser.getLikedTopics().removeIf(t -> t.getId().equals(this.id));
        } else {
            likedBy.add(likeUser);
            likeUser.getLikedTopics().add(this);
        }
    }

    public int getCommentCount() {
        return comments.size();
    }

    public String sumarize() {
        if (this.content == null)
            return null;
        String plainText = content.replaceAll("<[^>]*>", "");
        return plainText.length() > 150 ? plainText.substring(0, 150) : plainText;
    }

}
