package br.one.forum.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "profiles")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
public class Profile {

    @Id
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "photo")
    private String photo = "";

    public Profile(User user, String name) {
        this.user = user;
        this.name = name;
    }

    public Profile(String name, String photo) {
        this.name = name;
        this.photo = photo;
    }

}