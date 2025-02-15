package library.controllers;

import library.dtos.BorrowingRecordResponse;
import library.dtos.PatronRequest;
import library.dtos.PatronResponse;
import library.services.PatronService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatronControllerTest {
    @Mock
    private PatronService patronService;

    @InjectMocks
    private PatronController patronController;

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getAllPatrons_ReturnsListOfPatrons() {
        List<PatronResponse> patrons = List.of(
                new PatronResponse(1L, "Amir", "123-456-7890", "amir@example.com"),
                new PatronResponse(2L, "Ahmad", "098-765-4321", "ahmad@example.com")
        );
        when(patronService.getAllPatrons()).thenReturn(patrons);

        ResponseEntity<List<PatronResponse>> response = patronController.getAllPatrons();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("Amir", response.getBody().get(0).getName());
        assertEquals("Ahmad", response.getBody().get(1).getName());
    }

    @Test
    void createPatron_ValidRequest_ReturnsCreatedPatron() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        PatronRequest patronRequest = new PatronRequest("Amir", "123-456-7890", "amir@example.com");
        PatronResponse created = new PatronResponse(1L, "Amir", "123-456-7890", "amir@example.com");
        when(patronService.createPatron(any())).thenReturn(created);

        ResponseEntity<PatronResponse> response = patronController.createPatron(patronRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Amir", response.getBody().getName());
        assertEquals("amir@example.com", response.getBody().getEmail());
    }

    @Test
    void getPatronById_ExistingPatron_ReturnsPatron() {
        PatronResponse patron = new PatronResponse(1L, "Ahmad", "123-456-7890", "ahmad@example.com");
        when(patronService.getPatronById(1L)).thenReturn(patron);

        ResponseEntity<PatronResponse> response = patronController.getPatronById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Ahmad", response.getBody().getName());
        assertEquals("ahmad@example.com", response.getBody().getEmail());
    }

    @Test
    void updatePatron_ValidRequest_ReturnsUpdatedPatron() {
        PatronRequest request = new PatronRequest("Amir Updated", "987-654-3210", "amir.updated@example.com");
        PatronResponse updated = new PatronResponse(1L, "Amir Updated", "987-654-3210", "amir.updated@example.com");
        when(patronService.updatePatron(eq(1L), any())).thenReturn(updated);

        ResponseEntity<PatronResponse> response = patronController.updatePatron(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Amir Updated", response.getBody().getName());
        assertEquals("amir.updated@example.com", response.getBody().getEmail());
    }

    @Test
    void deletePatron_ExistingPatron_ReturnsNoContent() {
        ResponseEntity<Void> response = patronController.deletePatron(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(patronService).deletePatron(1L);
    }

    @Test
    void getBorrowingHistory_ExistingPatron_ReturnsBorrowingHistory() {
        List<BorrowingRecordResponse> borrowingHistory = List.of(
                new BorrowingRecordResponse(1L, 1L, "Clean Code", 1L, "Amir",
                        LocalDate.now(), LocalDate.now().plusWeeks(2), null)
        );
        when(patronService.getBorrowingHistory(1L)).thenReturn(borrowingHistory);

        ResponseEntity<List<BorrowingRecordResponse>> response = patronController.getBorrowingHistory(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals("Amir", response.getBody().get(0).getPatronName());
    }
}
