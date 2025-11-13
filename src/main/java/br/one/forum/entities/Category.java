package br.one.forum.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(
        name = "categories", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Category {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    @ToString.Include
    private String name;

    @ManyToMany(mappedBy = "categories")
    @Setter(AccessLevel.NONE)
    private Set<Topic> topics = new HashSet<>();

    public Category(@NotNull String name) {
        setName(name);
    }

    void setName(@NotNull String name) {
        this.name = name.toUpperCase();
    }

    public void addTopics(List<Topic> topics) {
        this.topics.addAll(topics);
    }

    public Category addTopic(Topic topic) {
        this.topics.add(topic);
        return this;
    }
}