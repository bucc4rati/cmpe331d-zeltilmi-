package com.flightroster.flightinfo.loadstress.controller;

import com.flightroster.flightinfo.controller.FlightController;
import com.flightroster.flightinfo.entity.Flight;
import com.flightroster.flightinfo.service.FlightService;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Load and Stress Testing for Flight Controller
 * Tests concurrent requests and system behavior under load
 */
@WebMvcTest(FlightController.class)
@ActiveProfiles("test")
class FlightLoadStressTest {

    private static final Logger logger = LoggerFactory.getLogger(FlightLoadStressTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    @RepeatedTest(10)
    void testRepeatedRequests_shouldHandleConsistently() throws Exception {
        logger.info("Repeated test execution: GET /api/flights");
        
        when(flightService.getAllFlights()).thenReturn(List.of(new Flight()));
        
        mockMvc.perform(get("/api/flights"))
                .andExpect(status().isOk());
    }

    @Test
    void testConcurrentRequests_shouldHandleMultipleUsers() throws Exception {
        logger.info("Testing concurrent requests with 10 threads");
        
        int numberOfThreads = 10;
        int requestsPerThread = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        when(flightService.getAllFlights()).thenReturn(List.of(new Flight()));
        
        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < requestsPerThread; j++) {
                        mockMvc.perform(get("/api/flights"))
                                .andExpect(status().isOk());
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    logger.error("Concurrent request failed", e);
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        int totalRequests = numberOfThreads * requestsPerThread;
        logger.info("Concurrent test results: {} successful, {} failed out of {} total requests", 
                successCount.get(), failureCount.get(), totalRequests);
        
        assert successCount.get() == totalRequests : 
            "Expected " + totalRequests + " successful requests, got " + successCount.get();
    }

    @Test
    void testStressLoad_highVolumeRequests() throws Exception {
        logger.info("Testing stress load with 100 sequential requests");
        
        when(flightService.getAllFlights()).thenReturn(List.of(new Flight()));
        
        long startTime = System.currentTimeMillis();
        int requestCount = 100;
        
        for (int i = 0; i < requestCount; i++) {
            mockMvc.perform(get("/api/flights"))
                    .andExpect(status().isOk());
        }
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double avgResponseTime = (double) totalTime / requestCount;
        
        logger.info("Stress test completed: {} requests in {} ms, average {} ms per request", 
                requestCount, totalTime, avgResponseTime);
    }
}

