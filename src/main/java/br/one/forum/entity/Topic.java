package br.one.forum.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "topics")
public class Topic extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Integer id;


    @Column(nullable = false)
    @ToString.Include
    private String title;


    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    @ToString.Include
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnore
    private User author;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "category_has_topic",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Setter(AccessLevel.NONE)
    @JsonIgnore

    private Set<Category> categories = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "likes",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private Set<User> likedBy = new HashSet<>();

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private Set<Comment> comments = new HashSet<>();

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

        if (likedBy.stream().anyMatch(u -> u.getId().equals(likeUser.getId()))){
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
        if (this.content == null) return null;
        String plainText = content.replaceAll("<[^>]*>", "");
        return plainText.length() > 150 ? plainText.substring(0, 150) + "..." : plainText;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return Objects.equals(id, topic.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
