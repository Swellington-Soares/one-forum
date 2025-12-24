package br.one.forum.entity;


import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class Profile {
    private String name;
    private String photo;
    private String bio;
}
