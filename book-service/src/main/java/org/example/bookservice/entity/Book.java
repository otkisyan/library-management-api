package org.example.bookservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "books_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genres_id",
            nullable = false))
    private Set<Genre> genres = new LinkedHashSet<>();

    @Column(nullable = true)
    private Double averageRating;

    @JsonIgnore
    @OneToMany(mappedBy = "book", orphanRemoval = true)
    private Set<BookView> bookViews = new LinkedHashSet<>();

}
