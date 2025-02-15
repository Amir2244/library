package library.services;

import library.entities.Book;
import library.entities.BorrowingRecord;
import library.entities.Patron;
import library.repositories.BookRepository;
import library.repositories.BorrowingRecordRepository;
import library.repositories.PatronRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorrowingServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private PatronRepository patronRepository;
    @Mock
    private BorrowingRecordRepository borrowingRecordRepository;
    @InjectMocks
    private BorrowingService borrowingService;

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

    private Patron createTestPatron(Long id, String name) {
        Patron patron = new Patron();
        patron.setId(id);
        patron.setName(name);
        patron.setEmail(name.toLowerCase() + "@test.com");
        patron.setContactInfo("123-456-7890");
        return patron;
    }

    @Test
    void borrowBook_WhenBookAvailable_ShouldSucceed() {
        Book book = createTestBook(1L, "Test Book", true);
        Patron patron = createTestPatron(1L, "John Doe");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(patronRepository.findById(1L)).thenReturn(Optional.of(patron));

        borrowingService.borrowBook(1L, 1L);

        verify(borrowingRecordRepository).save(any());
        verify(bookRepository).save(argThat(b -> !b.isAvailable()));
    }

    @Test
    void returnBook_WhenBookBorrowed_ShouldSucceed() {
        Book book = createTestBook(1L, "Test Book", false);
        Patron patron = createTestPatron(1L, "John Doe");
        BorrowingRecord record = new BorrowingRecord();
        record.setBook(book);
        record.setPatron(patron);

        when(borrowingRecordRepository.findByBookIdAndPatronIdAndReturnDateIsNull(1L, 1L))
                .thenReturn(Optional.of(record));

        borrowingService.returnBook(1L, 1L);

        verify(borrowingRecordRepository).save(any());
        verify(bookRepository).save(argThat(Book::isAvailable));
    }
}
