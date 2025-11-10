package br.one.forum.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"likedBy", "comments", "user", "categories"})
@Entity
@Table(name = "topic")
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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @ColumnDefault("current_timestamp()")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "category_has_topic",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "likes",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likedBy = new HashSet<>();

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    public Topic(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        Class<?> oClass = object instanceof HibernateProxy
                ? ((HibernateProxy) object).getHibernateLazyInitializer().getPersistentClass()
                : object.getClass();
        if (getClass() != oClass) return false;
        Topic other = (Topic) object;
        return id != null && id.equals(other.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(id);
    }

    public void toggleLike(User likeUser) {
        if (likeUser == null) return;
        if (likeUser.equals(user)) return;

        if (likedBy.contains(likeUser)) {
            likedBy.remove(likeUser);
            likeUser.getLikedTopics().remove(this);
        } else {
            likedBy.add(likeUser);
            likeUser.getLikedTopics().add(this);
        }
    }

    public int getLikeCount() {
        return likedBy.size();
    }

    public boolean isLikedByUser(User testUser) {
        return testUser != null && likedBy.stream().anyMatch(u -> Objects.equals(u.getId(), testUser.getId()));
    }

}
