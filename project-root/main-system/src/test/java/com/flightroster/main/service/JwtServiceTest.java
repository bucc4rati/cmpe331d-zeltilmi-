package com.flightroster.main.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Set test values for JWT secret and expiration
        ReflectionTestUtils.setField(jwtService, "secret", "mySecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L); // 24 hours

        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    void testGenerateToken_Success() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGenerateToken_WithExtraClaims() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ADMIN");

        String token = jwtService.generateToken(userDetails, extraClaims);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername_Success() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void testExtractExpiration_Success() {
        String token = jwtService.generateToken(userDetails);
        Date expiration = jwtService.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testValidateToken_ValidToken() {
        String token = jwtService.generateToken(userDetails);
        Boolean isValid = jwtService.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidUsername() {
        String token = jwtService.generateToken(userDetails);
        
        UserDetails differentUser = User.builder()
                .username("differentuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        Boolean isValid = jwtService.validateToken(token, differentUser);

        assertFalse(isValid);
    }

    @Test
    void testValidateToken_ExpiredToken() {
        // Set expiration to 1 millisecond
        ReflectionTestUtils.setField(jwtService, "expiration", 1L);
        
        String token = jwtService.generateToken(userDetails);
        
        // Wait for token to expire
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Expired token should return false
        Boolean isValid = jwtService.validateToken(token, userDetails);
        assertFalse(isValid);
    }
}

