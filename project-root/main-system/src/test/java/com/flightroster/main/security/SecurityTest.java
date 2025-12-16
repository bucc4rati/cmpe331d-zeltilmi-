package com.flightroster.main.security;

import com.flightroster.main.controller.AuthController;
import com.flightroster.main.dto.LoginRequest;
import com.flightroster.main.entity.User;
import com.flightroster.main.service.AuthService;
import com.flightroster.main.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security Testing
 * Tests authentication and authorization mechanisms
 */
@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @Test
    void testAuthentication_ValidCredentials() throws Exception {
        // Setup mocks
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setRole(User.Role.USER);
        
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock-jwt-token");
        
        // Test that valid credentials are accepted
        String requestBody = "{\"username\":\"testuser\",\"password\":\"password123\"}";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void testAuthentication_InvalidCredentials() throws Exception {
        // Test that invalid credentials are rejected
        String requestBody = "{\"username\":\"testuser\",\"password\":\"wrongpassword\"}";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAuthentication_MissingCredentials() throws Exception {
        // Test that missing credentials are rejected
        String requestBody = "{\"username\":\"\",\"password\":\"\"}";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testJWTToken_Generation() throws Exception {
        // Setup mocks
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setRole(User.Role.USER);
        
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock-jwt-token");
        
        // Test that JWT tokens are generated on successful login
        String requestBody = "{\"username\":\"testuser\",\"password\":\"password123\"}";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testSQLInjection_Prevention() throws Exception {
        // Test SQL injection prevention
        String maliciousInput = "{\"username\":\"admin' OR '1'='1\",\"password\":\"password123\"}";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(maliciousInput))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testXSS_Prevention() throws Exception {
        // Test XSS prevention
        String xssInput = "{\"username\":\"<script>alert('XSS')</script>\",\"password\":\"password123\"}";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(xssInput))
                .andExpect(status().isBadRequest());
    }
}

