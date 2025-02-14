package library.services;

import jakarta.transaction.Transactional;
import library.dtos.BookRequest;
import library.dtos.BookResponse;
import library.entities.Book;
import library.exceptions.ConflictException;
import library.exceptions.ResourceNotFoundException;
import library.repositories.BookRepository;
import library.repositories.BorrowingRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final BorrowingRecordRepository borrowingRecordRepository;
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::mapToBookResponse)
                .toList();
    }

    public BookResponse getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::mapToBookResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    @Transactional
    public BookResponse createBook(BookRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new ConflictException("ISBN already exists");
        }

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublicationYear(request.getPublicationYear());
        book.setIsbn(request.getIsbn());

        return mapToBookResponse(bookRepository.save(book));
    }

    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        if (!book.getIsbn().equals(request.getIsbn()) &&
                bookRepository.existsByIsbn(request.getIsbn())) {
            throw new ConflictException("ISBN already exists");
        }

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublicationYear(request.getPublicationYear());
        book.setIsbn(request.getIsbn());

        return mapToBookResponse(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        if (borrowingRecordRepository.existsByBookIdAndReturnDateIsNull(book.getId())) {
            throw new ConflictException("Cannot delete borrowed book");
        }

        bookRepository.delete(book);
    }

    private BookResponse mapToBookResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublicationYear(),
                book.getIsbn(),
                book.isAvailable()
        );
    }
}