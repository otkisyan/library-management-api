package org.example.bookservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.bookservice.dto.book.BookRatingUpdateEvent;
import org.example.bookservice.dto.book.BookRequestDto;
import org.example.bookservice.entity.Author;
import org.example.bookservice.entity.Book;
import org.example.bookservice.entity.Genre;
import org.example.bookservice.repository.AuthorRepository;
import org.example.bookservice.repository.BookRepository;
import org.example.bookservice.repository.GenreRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Log4j2
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public boolean existsById(Long bookId) {
        return bookRepository.existsById(bookId);
    }

    public Set<Genre> getBookGenres(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Book not found with id: " + bookId));
        return book.getGenres();
    }

    public Book saveBook(BookRequestDto bookRequestDto) {
        System.out.println(bookRequestDto.authorId());
        Author author = authorRepository.findById(bookRequestDto.authorId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Author not found with id: " + bookRequestDto.authorId()));

        List<Genre> genres = genreRepository.findAllById(bookRequestDto.genreIds());
        if (genres.isEmpty()) {
            throw new IllegalArgumentException("No genres found for the provided IDs");
        }

        Book book = new Book();
        book.setTitle(bookRequestDto.title());
        book.setAuthor(author);
        book.getGenres().addAll(genres);
        return bookRepository.save(book);
    }

    public Book updateBook(Long bookId, BookRequestDto bookRequestDto) {
        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + bookId));
        Author author = authorRepository.findById(bookRequestDto.authorId())
                .orElseThrow(() -> new IllegalArgumentException("Author not found with id: " + bookRequestDto.authorId()));

        existingBook.setTitle(bookRequestDto.title());
        existingBook.setAuthor(author);
        existingBook.getGenres().clear();

        List<Genre> genres = genreRepository.findAllById(bookRequestDto.genreIds());
        if (genres.isEmpty()) {
            throw new IllegalArgumentException("No genres found for the provided IDs");
        }

        existingBook.getGenres().addAll(genres);
        return bookRepository.save(existingBook);
    }

    public void deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Book not found with id: " + bookId));
        bookRepository.delete(book);
    }

    @KafkaListener(topics = "book.rating.update")
    public void consumeBookRatingUpdateTopic(BookRatingUpdateEvent bookRatingUpdateEvent) {
        log.info(bookRatingUpdateEvent);
        Book book = bookRepository.findById(bookRatingUpdateEvent.bookId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Book not found with id: " + bookRatingUpdateEvent.bookId()));

        book.setAverageRating(bookRatingUpdateEvent.averageRating());
        bookRepository.save(book);
    }
}
