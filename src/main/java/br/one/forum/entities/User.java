package br.one.forum.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public final class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Integer id;

    @Size(max = 255)
    @Column(unique = true, nullable = false)
    @ToString.Include
    private String email;

    @NotNull
    @Column(nullable = false, length = 64)
    private String password;

    @Column(name = "created_at", nullable = false, updatable = false)
    @ToString.Include
    @Setter(AccessLevel.NONE)
    private Instant createdAt = Instant.now();

    @Column(name = "update_at", nullable = false)
    @Setter(AccessLevel.NONE)
    private Instant updateAt = Instant.now();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;

    @ManyToMany(mappedBy = "likedBy", fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    private Set<Topic> likedTopics = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private Set<Topic> createdTopics = new HashSet<>();

    public User(@NotNull String email, @NotNull String password, @NotNull Profile profile) {
        this.email = email;
        this.password = password;
        this.profile = profile;
    }

    public User(@Email @NotBlank String email, @NotBlank @Size(min = 4) String password) {
    }

    @PrePersist
    private void onCreate() {
        if (createdAt == null)
            createdAt = Instant.now();
    }

    @PreUpdate
    private void onUpdate() {
        updateAt = Instant.now();
    }

    @ToString.Include(name = "password")
    private String maskedPassword() {
        return "[PROTECTED]";
    }

}
