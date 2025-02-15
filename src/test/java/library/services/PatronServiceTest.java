package library.services;

import library.dtos.PatronRequest;
import library.dtos.PatronResponse;
import library.entities.Patron;
import library.repositories.PatronRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatronServiceTest {
    @Mock
    private PatronRepository patronRepository;
    @InjectMocks
    private PatronService patronService;

    @Test
    void getAllPatrons_ShouldReturnAllPatrons() {
        List<Patron> patrons = List.of(
            createTestPatron(1L, "John Doe"),
            createTestPatron(2L, "Jane Doe")
        );
        when(patronRepository.findAll()).thenReturn(patrons);

        List<PatronResponse> result = patronService.getAllPatrons();

        assertEquals(2, result.size());
        verify(patronRepository).findAll();
    }

    @Test
    void createPatron_WithValidData_ShouldSucceed() {
        PatronRequest request = new PatronRequest("John Doe", "123-456-7890", "john@test.com");
        Patron savedPatron = createTestPatron(1L, "John Doe");
        when(patronRepository.save(any())).thenReturn(savedPatron);

        PatronResponse result = patronService.createPatron(request);

        assertEquals("John Doe", result.getName());
        verify(patronRepository).save(any());
    }

    private Patron createTestPatron(Long id, String name) {
        Patron patron = new Patron();
        patron.setId(id);
        patron.setName(name);
        patron.setEmail(name.toLowerCase().replace(" ", ".") + "@test.com");
        patron.setContactInfo("123-456-7890");
        return patron;
    }
}
