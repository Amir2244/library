package library.services;

import jakarta.transaction.Transactional;
import library.entities.Book;
import library.entities.BorrowingRecord;
import library.entities.Patron;
import library.exceptions.ConflictException;
import library.exceptions.ResourceNotFoundException;
import library.repositories.BookRepository;
import library.repositories.BorrowingRecordRepository;
import library.repositories.PatronRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BorrowingService {

    private final BookRepository bookRepository;
    private final PatronRepository patronRepository;
    private final BorrowingRecordRepository borrowingRecordRepository;

    public void borrowBook(Long bookId, Long patronId) {
        // 1. Validate existence
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        Patron patron = patronRepository.findById(patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found with id: " + patronId));

        // 2. Check availability
        if (!book.isAvailable()) {
            throw new ConflictException("Book is already borrowed");
        }

        // 3. Check for existing active borrow record
        if (borrowingRecordRepository.existsByBookIdAndReturnDateIsNull(bookId)) {
            throw new ConflictException("Book is currently borrowed by another patron");
        }

        // 4. Create borrowing record
        BorrowingRecord record = new BorrowingRecord();
        record.setBook(book);
        record.setPatron(patron);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusWeeks(2)); // 2-week borrowing period
        borrowingRecordRepository.save(record);

        // 5. Update book availability
        book.setAvailable(false);
        bookRepository.save(book);
    }

    public void returnBook(Long bookId, Long patronId) {
        // 1. Find active borrowing record
        BorrowingRecord record = borrowingRecordRepository
                .findByBookIdAndPatronIdAndReturnDateIsNull(bookId, patronId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active borrowing record found for book ID " + bookId + " and patron ID " + patronId
                ));

        // 2. Update return date
        record.setReturnDate(LocalDate.now());
        borrowingRecordRepository.save(record);

        // 3. Update book availability
        Book book = record.getBook();
        book.setAvailable(true);
        bookRepository.save(book);
    }

}