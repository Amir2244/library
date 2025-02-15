package library.controllers;

import library.services.BorrowingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BorrowingControllerTest {
    @Mock
    private BorrowingService borrowingService;

    @InjectMocks
    private BorrowingController borrowingController;

    @Test
    void borrowBook_ValidRequest_ReturnsOk() {
        ResponseEntity<Void> response = borrowingController.borrowBook(1L, 1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(borrowingService).borrowBook(1L, 1L);
    }

    @Test
    void returnBook_ValidRequest_ReturnsOk() {
        ResponseEntity<Void> response = borrowingController.returnBook(1L, 1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(borrowingService).returnBook(1L, 1L);
    }
}
