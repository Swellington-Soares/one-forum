package br.one.forum.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Integer id;

    @Size(max = 255)
    @Column(unique = true, nullable = false)
    @ToString.Include
    private String email;

    @NotBlank
    @Column(nullable = false, length = 64)
    private String password;

    @Column(name = "created_at", nullable = false, updatable = false)
    @ToString.Include
    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "update_at", nullable = false)
    @Setter(AccessLevel.NONE)
    @UpdateTimestamp
    private Instant updateAt;

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

    @Column
    private boolean emailVerified = false;

    @Column
    private boolean locked = false;

    @Column
    @Accessors(chain = true)
    private boolean deleted = false;

    public User(@NotNull String email,
                @NotNull String password,
                @NotNull Profile profile) {
        this.email = email;
        this.password = password;
        this.profile = profile;
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
