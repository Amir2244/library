package library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import library.dtos.PatronRequest;
import library.entities.Role;
import library.entities.User;
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
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PatronControllerIntegrationTest {

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
    void getAllPatrons_WithValidToken_ReturnsPatrons() throws Exception {
        mockMvc.perform(get("/api/patrons")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void createPatron_WithValidData_ReturnsCreatedPatron() throws Exception {
        PatronRequest request = new PatronRequest(
                "Amir",
                "123-456-7890",
                "amir.test@example.com"
        );

        mockMvc.perform(post("/api/patrons")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Amir"))
                .andExpect(jsonPath("$.email").value("amir.test@example.com"))
                .andExpect(jsonPath("$.contactInfo").value("123-456-7890"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void getPatronById_ExistingPatron_ReturnsPatron() throws Exception {
        PatronRequest createRequest = new PatronRequest(
                "Amir",
                "123-456-7890",
                "amir@test.com"
        );

        MvcResult createResult = mockMvc.perform(post("/api/patrons")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        Integer patronId = JsonPath.read(responseContent, "$.id");

        mockMvc.perform(get("/api/patrons/{id}", patronId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Amir"))
                .andExpect(jsonPath("$.email").value("amir@test.com"));
    }


}
