package br.one.forum.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    private String email;

    private String password;


    @Embedded
    private Profile profile;

    @ManyToMany(mappedBy = "likedBy", fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    private Set<Topic> likedTopics = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private Set<Topic> createdTopics = new HashSet<>();

    @Column
    private boolean emailVerified = false;

    @Column
    private boolean locked = false;

    @Column
    private boolean deleted = false;

    private String refreshToken;
    private Instant refreshTokenExpiration;

    @ToString.Include(name = "password")
    private String maskedPassword() {
        return "[PROTECTED]";
    }

    public int getCommentsCount() {
        return comments.size();
    }

    public int getTopicCreatedCount() {
        return createdTopics.size();
    }

    @Builder
    public User(Long id, String email, String password, Profile profile, boolean locked, boolean deleted) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.locked = locked;
        this.deleted = deleted;
    }


    public void addTopic(Topic topic) {
        topic.setAuthor(this);
        createdTopics.add(topic);
    }

    public void removeTopic(Topic topic) {
        if (createdTopics.contains(topic)) {
            topic.setAuthor(null);
            createdTopics.remove(topic);
        }
    }

    public void removeAllTopic() {
        createdTopics.forEach(
                x -> x.setAuthor(null)
        );
        createdTopics.clear();
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setAuthor(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
