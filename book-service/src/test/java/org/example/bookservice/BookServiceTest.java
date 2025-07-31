package org.example.bookservice;

import org.example.bookservice.entity.Book;
import org.example.bookservice.entity.BookView;
import org.example.bookservice.repository.BookRepository;
import org.example.bookservice.repository.BookViewRepository;
import org.example.bookservice.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookViewRepository bookViewRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void When_BooksHaveDifferentUserOverlap_Expect_MoreSimilarBooksReturnedFirst() {
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        UUID user3 = UUID.randomUUID();

        Book bookA = new Book();
        Book bookB = new Book();
        Book bookC = new Book();
        bookA.setId(1L);
        bookB.setId(2L);
        bookC.setId(3L);

        List<BookView> mockViews = List.of(
                new BookView(1L, user1, bookA),
                new BookView(2L, user2, bookA),

                new BookView(3L, user1, bookB),
                new BookView(4L, user2, bookB),

                new BookView(5L, user1, bookC),
                new BookView(6L, user3, bookC)
        );

        when(bookViewRepository.findAll()).thenReturn(mockViews);
        when(bookService.findBooksByIds(List.of(2L, 3L)))
                .thenReturn(List.of(bookB, bookC));

        List<Book> result = bookService.getSimilarBooks(1L, 2);

        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId()); // Book B first (higher similarity)
        assertEquals(3L, result.get(1).getId()); // Book C second
    }

}
