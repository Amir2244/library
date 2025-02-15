package library.controllers;

import library.entities.Book;
import library.entities.Patron;
import library.entities.Role;
import library.entities.User;
import library.repositories.BookRepository;
import library.repositories.BorrowingRecordRepository;
import library.repositories.PatronRepository;
import library.repositories.UserRepository;
import library.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Year;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BorrowingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PatronRepository patronRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String authToken;
    private Book testBook;
    private Patron testPatron;

    @BeforeEach
    void setUp() {
        borrowingRecordRepository.deleteAll();
        bookRepository.deleteAll();
        patronRepository.deleteAll();
        userRepository.deleteAll();

        User testUser = new User();
        testUser.setEmail("librarian@test.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setRole(Role.LIBRARIAN);
        userRepository.save(testUser);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(testUser.getEmail())
                .password(testUser.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + testUser.getRole().name())))
                .build();

        authToken = jwtService.generateToken(userDetails);

        testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setIsbn("978-0-123456-47-2");
        testBook.setPublicationYear(Year.of(Year.now().getValue()));
        testBook.setAvailable(true);
        testBook = bookRepository.save(testBook);

        testPatron = new Patron();
        testPatron.setName("Test Patron");
        testPatron.setEmail("patron@test.com");
        testPatron.setContactInfo("123-456-7890");
        testPatron = patronRepository.save(testPatron);
    }

    @Test
    void borrowBook_ValidRequest_ReturnsOk() throws Exception {
        assertTrue(testBook.isAvailable());

        mockMvc.perform(post("/api/borrow/{bookId}/patron/{patronId}", testBook.getId(), testPatron.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        Book updatedBook = bookRepository.findById(testBook.getId()).orElseThrow();
        assertFalse(updatedBook.isAvailable());
    }

    @Test
    void returnBook_ValidRequest_ReturnsOk() throws Exception {
        mockMvc.perform(post("/api/borrow/{bookId}/patron/{patronId}", testBook.getId(), testPatron.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/return/{bookId}/patron/{patronId}", testBook.getId(), testPatron.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        Book updatedBook = bookRepository.findById(testBook.getId()).orElseThrow();
        assertTrue(updatedBook.isAvailable());
    }
}
