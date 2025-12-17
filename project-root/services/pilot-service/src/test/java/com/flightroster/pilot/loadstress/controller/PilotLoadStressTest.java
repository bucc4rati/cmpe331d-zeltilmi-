package com.flightroster.pilot.loadstress.controller;

import com.flightroster.pilot.controller.PilotController;

import com.flightroster.pilot.entity.Pilot;
import com.flightroster.pilot.service.PilotService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Load and Stress Testing for Pilot Controller
 * Tests concurrent requests and system behavior under load
 */
@WebMvcTest(PilotController.class)
@ActiveProfiles("test")
class PilotLoadStressTest {

    private static final Logger logger = LoggerFactory.getLogger(PilotLoadStressTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PilotService pilotService;

    @RepeatedTest(10)
    void testRepeatedRequests_shouldHandleConsistently() throws Exception {
        logger.info("Repeated test execution: GET /api/pilots");
        
        when(pilotService.getAllPilots()).thenReturn(List.of(new Pilot()));
        
        mockMvc.perform(get("/api/pilots"))
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
        
        when(pilotService.getAllPilots()).thenReturn(List.of(new Pilot()));
        
        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < requestsPerThread; j++) {
                        mockMvc.perform(get("/api/pilots"))
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
        
        when(pilotService.getAllPilots()).thenReturn(List.of(new Pilot()));
        
        long startTime = System.currentTimeMillis();
        int requestCount = 100;
        
        for (int i = 0; i < requestCount; i++) {
            mockMvc.perform(get("/api/pilots"))
                    .andExpect(status().isOk());
        }
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double avgResponseTime = (double) totalTime / requestCount;
        
        logger.info("Stress test completed: {} requests in {} ms, average {} ms per request", 
                requestCount, totalTime, avgResponseTime);
    }

    @Test
    void testBulkAvailabilityUpdate_stressTest() throws Exception {
        logger.info("Testing bulk availability update under stress");
        
        when(pilotService.updateBulkAvailability(any(), any())).thenReturn(List.of(new Pilot()));
        
        long startTime = System.currentTimeMillis();
        int requestCount = 50;
        
        for (int i = 0; i < requestCount; i++) {
            String json = "[1,2,3]";
            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/pilots/bulk-availability")
                            .param("isAvailable", "true")
                            .contentType("application/json")
                            .content(json))
                    .andExpect(status().isOk());
        }
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double avgResponseTime = (double) totalTime / requestCount;
        
        logger.info("Bulk update stress test: {} requests in {} ms, average {} ms per request", 
                requestCount, totalTime, avgResponseTime);
    }
}

