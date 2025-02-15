package library.controllers;

import library.dtos.AuthRequest;
import library.dtos.AuthResponse;
import library.dtos.RegisterRequest;
import library.entities.Role;
import library.entities.User;
import library.exceptions.ConflictException;
import library.repositories.UserRepository;
import library.services.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthController authController;

    @Test
    void login_ValidCredentials_ReturnsToken() {

        AuthRequest request = new AuthRequest("test@example.com", "password");
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("test@example.com")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_PATRON")))
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("test.jwt.token");


        ResponseEntity<AuthResponse> response = authController.login(request);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test.jwt.token", response.getBody().getToken());
    }

    @Test
    void register_NewUser_ReturnsToken() {

        RegisterRequest request = new RegisterRequest("test@example.com", "password");
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("test@example.com")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_PATRON")))
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("test.jwt.token");


        ResponseEntity<AuthResponse> response = authController.register(request);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test.jwt.token", response.getBody().getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ExistingEmail_ThrowsConflictException() {

        RegisterRequest request = new RegisterRequest("existing@example.com", "password");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);


        assertThrows(ConflictException.class, () -> authController.register(request));
    }
}
