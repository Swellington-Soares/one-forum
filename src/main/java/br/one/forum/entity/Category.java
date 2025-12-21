package br.one.forum.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    @ToString.Include
    private String name;

    @ManyToMany(mappedBy = "categories")
    @Setter(AccessLevel.NONE)
    private Set<Topic> topics = new HashSet<>();

    public Category(String name) {
        setName(name);
    }

    void setName(String name) {
        this.name = name.toUpperCase();
    }

    public void addTopics(List<Topic> topics) {
        this.topics.addAll(topics);
    }

    public void addTopic(Topic topic) {
        this.topics.add(topic);
    }
}
