package org.example.bookservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.bookservice.dto.book.BookRequestDto;
import org.example.bookservice.entity.Book;
import org.example.bookservice.entity.Genre;
import org.example.bookservice.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @GetMapping()
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }
    @GetMapping("/{bookId}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookService.existsById(bookId));
    }

    @GetMapping("/{bookId}/genres")
    public ResponseEntity<Set<Genre>> getBookGenres(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookService.getBookGenres(bookId));
    }

    @PostMapping()
    public ResponseEntity<Book> saveBook(@RequestBody @Valid BookRequestDto bookRequestDto) {
        return ResponseEntity.ok(bookService.saveBook(bookRequestDto));
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<Book> updateBook(@RequestBody @Valid BookRequestDto bookRequestDto,
                                           @PathVariable Long bookId) {
        return ResponseEntity.ok(bookService.updateBook(bookId, bookRequestDto));
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.ok().build();
    }
}
