package org.example.bookservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.bookservice.dto.book.BookRatingUpdateEvent;
import org.example.bookservice.dto.book.BookRequestDto;
import org.example.bookservice.dto.book.SimilarBook;
import org.example.bookservice.entity.Author;
import org.example.bookservice.entity.Book;
import org.example.bookservice.entity.BookView;
import org.example.bookservice.entity.Genre;
import org.example.bookservice.repository.AuthorRepository;
import org.example.bookservice.repository.BookRepository;
import org.example.bookservice.repository.BookViewRepository;
import org.example.bookservice.repository.GenreRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class BookService {

    private final BookRepository bookRepository;
    private final BookViewRepository bookViewRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long bookId) {
        return bookRepository.findById(bookId).orElseThrow(RuntimeException::new);
    }

    public BookView saveBookView(Book book, @AuthenticationPrincipal(expression = "claims['sub']") String userId){
        log.info(userId);
        log.info("Saving book view");
        return bookViewRepository.save(BookView.builder()
                .userId(UUID.fromString(userId))
                .book(book)
                .build());
    }

    public boolean existsById(Long bookId) {
        return bookRepository.existsById(bookId);
    }

    /**
     * Retrieves a list of books similar to the given book based on item-based collaborative filtering
     * using cosine similarity of user view counts.
     * <p>
     * The similarity between two books is computed as the cosine similarity between their respective
     * user view count vectors:
     * <pre>
     * \text{sim}(i, j) = \cos(\vec{i}, \vec{j}) = \frac{\vec{i} \cdot \vec{j}}{\|\vec{i}\|_{2}\cdot \|\vec{j}\|_{2}}
     * </pre>
     * where each vector component corresponds to the number of times a user viewed the book.
     * <p>
     * <p>Example of calculating similarity between books B1 and B3:</p>
     *
     * <pre>
     * \[
     * \text{sim}(B_1, B_3) = \frac{1 \cdot 0 + 1 \cdot 1 + 1 \cdot 1}{\sqrt{1^2 + 1^2 + 1^2} \times \sqrt{0^2 + 1^2 + 1^2}} = \frac{2}{\sqrt{3} \times \sqrt{2}} \approx 0.816
     * \]
     * </pre>
     *
     * <p>Table of book views by users:</p>
     *
     * <table border="1">
     *   <thead>
     *     <tr>
     *       <th>User</th>
     *       <th>B1</th>
     *       <th>B2</th>
     *       <th>B3</th>
     *     </tr>
     *   </thead>
     *   <tbody>
     *     <tr>
     *       <td>U1</td>
     *       <td>1</td>
     *       <td>1</td>
     *       <td>0</td>
     *     </tr>
     *     <tr>
     *       <td>U2</td>
     *       <td>1</td>
     *       <td>0</td>
     *       <td>1</td>
     *     </tr>
     *     <tr>
     *       <td>U3</td>
     *       <td>1</td>
     *       <td>1</td>
     *       <td>1</td>
     *     </tr>
     *   </tbody>
     * </table>
     * The method returns the top N most similar books to the specified book, excluding the book itself.
     *
     *
     * @param bookId the ID of the target book to find similar books for
     * @param topN the maximum number of similar books to return
     * @return a list of books most similar to the target book, sorted by similarity in descending order
     */
    public List<Book> getSimilarBooks(Long bookId, int topN){
        List<BookView> allViews = bookViewRepository.findAll();

        // bookId -> (userId -> count)
        Map<Long, Map<UUID, Integer>> bookToUserCounts = new HashMap<>();

        for (BookView view : allViews) {
            Long viewBookId = view.getBook().getId();
            UUID userId = view.getUserId();

            bookToUserCounts
                    .computeIfAbsent(viewBookId, k -> new HashMap<>())
                    .merge(userId, 1, Integer::sum); // aggregate users book views
        }

        Map<UUID, Integer> targetVec = bookToUserCounts.getOrDefault(bookId, Map.of());
        double targetNorm = Math.sqrt(
                targetVec.values().stream().mapToDouble(v -> v * v).sum()
        );

        List<SimilarBook> similarBooks = new ArrayList<>();

        for (var entry : bookToUserCounts.entrySet()) {
            Long otherBookId = entry.getKey();
            if (otherBookId.equals(bookId)) continue;

            Map<UUID, Integer> otherVec = entry.getValue();

            double dot = 0.0;
            for (UUID user : targetVec.keySet()) {
                int a = targetVec.getOrDefault(user, 0);
                int b = otherVec.getOrDefault(user, 0);
                dot += a * b;
            }

            double otherNorm = Math.sqrt(
                    otherVec.values().stream().mapToDouble(v -> v * v).sum()
            );

            double similarity = (targetNorm == 0 || otherNorm == 0)
                    ? 0
                    : dot / (targetNorm * otherNorm);

            similarBooks.add(new SimilarBook(otherBookId, similarity));
        }

        List<Long> bookIds = similarBooks.stream()
                .sorted(Comparator.comparingDouble(SimilarBook::score).reversed())
                .limit(topN)
                .map(SimilarBook::bookId)
                .toList();

        List<Book> books = findBooksByIds(bookIds);

        Map<Long, Book> bookMap = books.stream()
                .collect(Collectors.toMap(Book::getId, Function.identity()));

        return bookIds.stream()
                .map(bookMap::get)
                .toList();
    }

    public List<Book> findBooksByIds(List<Long> bookIds){
        return bookRepository.findAllById(bookIds);
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
