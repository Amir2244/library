package library.controllers;

import library.dtos.BookRequest;
import library.dtos.BookResponse;
import library.services.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {
    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @Test
    void getAllBooks_ReturnsListOfBooks() {
        List<BookResponse> books = List.of(new BookResponse(1L, "Clean Code", "Robert Martin", Year.of(2008), "ISBN1", true), new BookResponse(2L, "Design Patterns", "Gang of Four", Year.of(1994), "ISBN2", true));
        when(bookService.getAllBooks()).thenReturn(books);

        ResponseEntity<List<BookResponse>> response = bookController.getAllBooks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getBookById_ExistingBook_ReturnsBook() {
        BookResponse book = new BookResponse(1L, "Clean Code", "Robert Martin", Year.of(2008), "ISBN1", true);
        when(bookService.getBookById(1L)).thenReturn(book);

        ResponseEntity<BookResponse> response = bookController.getBookById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Clean Code", response.getBody().getTitle());
    }

    @Test
    void createBook_ValidRequest_ReturnsCreatedBook() {
        BookRequest request = new BookRequest("New Book", "Author", Year.of(2024), "ISBN123");
        BookResponse created = new BookResponse(1L, "New Book", "Author", Year.of(2024), "ISBN123", true);
        when(bookService.createBook(any())).thenReturn(created);

        ResponseEntity<BookResponse> response = bookController.createBook(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
