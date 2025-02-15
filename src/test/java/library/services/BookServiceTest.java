package library.services;

import library.dtos.BookRequest;
import library.dtos.BookResponse;
import library.entities.Book;
import library.exceptions.ConflictException;
import library.repositories.BookRepository;
import library.repositories.BorrowingRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BorrowingRecordRepository borrowingRecordRepository;
    @InjectMocks
    private BookService bookService;

    private Book createTestBook(Long id, String title, boolean available) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor("Test Author");
        book.setPublicationYear(Year.of(2024));
        book.setIsbn("ISBN-" + id);
        book.setAvailable(available);
        return book;
    }

    @Test
    void getAllBooks_ReturnsAllBooks() {
        List<Book> books = List.of(
                createTestBook(1L, "Clean Code", true),
                createTestBook(2L, "Design Patterns", true)
        );
        when(bookRepository.findAll()).thenReturn(books);

        List<BookResponse> result = bookService.getAllBooks();

        assertEquals(2, result.size());
        verify(bookRepository).findAll();
    }

    @Test
    void getBookById_ExistingBook_ReturnsBook() {
        Book book = createTestBook(1L, "Clean Code", true);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookResponse result = bookService.getBookById(1L);

        assertEquals("Clean Code", result.getTitle());
    }

    @Test
    void createBook_ValidBook_ReturnsCreatedBook() {
        BookRequest request = new BookRequest("New Book", "Author", Year.of(2024), "ISBN123");
        Book savedBook = createTestBook(1L, "New Book", true);
        when(bookRepository.save(any())).thenReturn(savedBook);

        BookResponse result = bookService.createBook(request);

        assertEquals("New Book", result.getTitle());
        verify(bookRepository).save(any());
    }

    @Test
    void deleteBook_WithActiveBorrowing_ThrowsConflictException() {
        Book book = createTestBook(1L, "Test Book", true);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(borrowingRecordRepository.existsByBookIdAndReturnDateIsNull(1L))
                .thenReturn(true);

        assertThrows(ConflictException.class, () ->
                bookService.deleteBook(1L));
    }

}
