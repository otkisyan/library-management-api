package org.example.bookservice.repository;

import org.example.bookservice.entity.BookView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookViewRepository extends JpaRepository<BookView, Long> {
}