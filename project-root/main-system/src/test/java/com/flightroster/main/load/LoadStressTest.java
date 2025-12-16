package com.flightroster.main.load;

import com.flightroster.main.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Load and Stress Testing
 * Tests system behavior under high load and stress conditions
 */
@SpringBootTest
class LoadStressTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void testLoad_1000Requests() {
        // Load test: 1000 concurrent requests
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        ReflectionTestUtils.setField(jwtService, "secret", "mySecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);

        int requestCount = 1000;
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<Long> responseTimes = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < requestCount; i++) {
            long requestStart = System.currentTimeMillis();
            try {
                String token = jwtService.generateToken(userDetails);
                jwtService.validateToken(token, userDetails);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failureCount.incrementAndGet();
            }
            long requestEnd = System.currentTimeMillis();
            responseTimes.add(requestEnd - requestStart);
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // Calculate statistics
        double avgResponseTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        long maxResponseTime = responseTimes.stream().mapToLong(Long::longValue).max().orElse(0);
        long minResponseTime = responseTimes.stream().mapToLong(Long::longValue).min().orElse(0);

        // Log results
        System.out.println("Load Test Results (1000 requests):");
        System.out.println("Total time: " + totalTime + " ms");
        System.out.println("Successful requests: " + successCount.get());
        System.out.println("Failed requests: " + failureCount.get());
        System.out.println("Average response time: " + avgResponseTime + " ms");
        System.out.println("Max response time: " + maxResponseTime + " ms");
        System.out.println("Min response time: " + minResponseTime + " ms");
        System.out.println("Requests per second: " + (requestCount * 1000.0 / totalTime));

        // Assertions
        assertEquals(requestCount, successCount.get(), "All requests should succeed");
        assertTrue(avgResponseTime < 10, "Average response time should be acceptable");
    }

    @Test
    void testStress_ConcurrentUsers() throws InterruptedException {
        // Stress test: Multiple concurrent users
        ReflectionTestUtils.setField(jwtService, "secret", "mySecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);

        int userCount = 50;
        int operationsPerUser = 20;
        CountDownLatch latch = new CountDownLatch(userCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < userCount; i++) {
            final int userId = i;
            Thread thread = new Thread(() -> {
                UserDetails userDetails = User.builder()
                        .username("user" + userId)
                        .password("password")
                        .authorities("ROLE_USER")
                        .build();

                for (int j = 0; j < operationsPerUser; j++) {
                    try {
                        String token = jwtService.generateToken(userDetails);
                        jwtService.validateToken(token, userDetails);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                    }
                }
                latch.countDown();
            });
            thread.start();
        }

        latch.await();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        int totalOperations = userCount * operationsPerUser;

        // Log results
        System.out.println("Stress Test Results (" + userCount + " concurrent users):");
        System.out.println("Total operations: " + totalOperations);
        System.out.println("Total time: " + totalTime + " ms");
        System.out.println("Successful operations: " + successCount.get());
        System.out.println("Failed operations: " + failureCount.get());
        System.out.println("Operations per second: " + (totalOperations * 1000.0 / totalTime));

        // Assertions
        assertEquals(totalOperations, successCount.get(), "All operations should succeed");
        assertTrue(totalTime < 10000, "Stress test should complete in reasonable time");
    }

    @Test
    void testStress_MemoryUsage() {
        // Stress test: Memory usage under load
        ReflectionTestUtils.setField(jwtService, "secret", "mySecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);

        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        List<String> tokens = new ArrayList<>();
        int tokenCount = 10000;

        for (int i = 0; i < tokenCount; i++) {
            tokens.add(jwtService.generateToken(userDetails));
        }

        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = finalMemory - initialMemory;

        // Log results
        System.out.println("Memory Stress Test:");
        System.out.println("Tokens generated: " + tokenCount);
        System.out.println("Memory used: " + (memoryUsed / 1024 / 1024) + " MB");
        System.out.println("Memory per token: " + (memoryUsed / tokenCount) + " bytes");

        // Assertions
        assertTrue(memoryUsed < 100 * 1024 * 1024, "Memory usage should be reasonable");
        assertFalse(tokens.isEmpty(), "Tokens should be generated");
    }
}

