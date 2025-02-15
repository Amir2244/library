package library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import library.dtos.BookRequest;
import library.entities.User;
import library.entities.Role;
import library.repositories.UserRepository;
import library.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Year;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String authToken;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    void getAllBooks_WithValidToken_ReturnsBooks() throws Exception {
        mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void createBook_WithValidData_ReturnsCreatedBook() throws Exception {
        BookRequest request = new BookRequest(
                "Test Book",
                "Test Author",
                Year.of(2024),
                "978-0-123456-47-2"
        );

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"))
                .andExpect(jsonPath("$.publicationYear").value(2024))
                .andExpect(jsonPath("$.isbn").value("978-0-123456-47-2"));
    }

    @Test
    void getBookById_ExistingBook_ReturnsBook() throws Exception {
        // First create a book
        BookRequest createRequest = new BookRequest(
                "Test Book",
                "Test Author",
                Year.of(2024),
                "978-0-123456-47-2"
        );

        mockMvc.perform(post("/api/books")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)));

        // Then retrieve it
        mockMvc.perform(get("/api/books/1")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void updateBook_WithValidData_ReturnsUpdatedBook() throws Exception {
        // First create a book
        BookRequest createRequest = new BookRequest(
                "Test Book",
                "Test Author",
                Year.of(2024),
                "978-0-123456-47-2"
        );

        mockMvc.perform(post("/api/books")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)));

        // Then update it
        BookRequest updateRequest = new BookRequest(
                "Updated Book",
                "Updated Author",
                Year.of(2024),
                "978-0-123456-47-2"
        );

        mockMvc.perform(put("/api/books/1")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Book"));
    }

    @Test
    void deleteBook_ExistingBook_ReturnsNoContent() throws Exception {
        BookRequest createRequest = new BookRequest(
                "Test Book",
                "Test Author",
                Year.of(2024),
                "978-0-123456-47-2"
        );

        mockMvc.perform(post("/api/books")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)));

        mockMvc.perform(delete("/api/books/1")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());
    }
}
