package com.flightroster.main.performance;

import com.flightroster.main.service.AuthService;
import com.flightroster.main.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance Testing
 * Tests response time and performance metrics
 */
@SpringBootTest
class PerformanceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Test
    void testJWTGeneration_Performance() {
        // Test JWT token generation performance
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        ReflectionTestUtils.setField(jwtService, "secret", "mySecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);

        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 1000; i++) {
            jwtService.generateToken(userDetails);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Log performance metrics
        System.out.println("JWT Generation Performance:");
        System.out.println("1000 tokens generated in: " + duration + " ms");
        System.out.println("Average time per token: " + (duration / 1000.0) + " ms");
        
        // Assert that performance is acceptable (less than 1 second for 1000 tokens)
        assertTrue(duration < 1000, "JWT generation should be fast");
    }

    @Test
    void testJWTValidation_Performance() {
        // Test JWT token validation performance
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        ReflectionTestUtils.setField(jwtService, "secret", "mySecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);

        String token = jwtService.generateToken(userDetails);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 1000; i++) {
            jwtService.validateToken(token, userDetails);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Log performance metrics
        System.out.println("JWT Validation Performance:");
        System.out.println("1000 validations in: " + duration + " ms");
        System.out.println("Average time per validation: " + (duration / 1000.0) + " ms");
        
        // Assert that performance is acceptable (increased threshold for validation)
        assertTrue(duration < 2000, "JWT validation should be fast");
    }

    @Test
    void testConcurrentOperations_Performance() {
        // Test concurrent operations performance
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        ReflectionTestUtils.setField(jwtService, "secret", "mySecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);

        List<Thread> threads = new ArrayList<>();
        int threadCount = 10;
        int operationsPerThread = 100;

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    String token = jwtService.generateToken(userDetails);
                    jwtService.validateToken(token, userDetails);
                }
            });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        int totalOperations = threadCount * operationsPerThread * 2; // generate + validate

        // Log performance metrics
        System.out.println("Concurrent Operations Performance:");
        System.out.println(totalOperations + " operations completed in: " + duration + " ms");
        System.out.println("Operations per second: " + (totalOperations * 1000.0 / duration));
        
        assertTrue(duration < 5000, "Concurrent operations should complete in reasonable time");
    }
}

