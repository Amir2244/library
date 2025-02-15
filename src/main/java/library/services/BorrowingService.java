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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
@Transactional
public class BorrowingService {

    private final BookRepository bookRepository;
    private final PatronRepository patronRepository;
    private final BorrowingRecordRepository borrowingRecordRepository;
    @CacheEvict(value = {"borrowing-records", "books"}, allEntries = true)
    public void borrowBook(Long bookId, Long patronId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        Patron patron = patronRepository.findById(patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found with id: " + patronId));

        if (!book.isAvailable()) {
            throw new ConflictException("Book is already borrowed");
        }

        if (borrowingRecordRepository.existsByBookIdAndReturnDateIsNull(bookId)) {
            throw new ConflictException("Book is currently borrowed by another patron");
        }


        BorrowingRecord record = new BorrowingRecord();
        record.setBook(book);
        record.setPatron(patron);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusWeeks(2));
        borrowingRecordRepository.save(record);


        book.setAvailable(false);
        bookRepository.save(book);
    }
    @CacheEvict(value = {"borrowing-records", "books"}, allEntries = true)
    public void returnBook(Long bookId, Long patronId) {

        BorrowingRecord record = borrowingRecordRepository
                .findByBookIdAndPatronIdAndReturnDateIsNull(bookId, patronId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active borrowing record found for book ID " + bookId + " and patron ID " + patronId
                ));


        record.setReturnDate(LocalDate.now());
        borrowingRecordRepository.save(record);


        Book book = record.getBook();
        book.setAvailable(true);
        bookRepository.save(book);
    }

}