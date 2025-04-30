package com.jrj.transaction.stress;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionApiStressTest {

    @LocalServerPort
    private int port;

    private final String testJsonContent = """
        {
            "type": "D",
            "accountId": "my-account-001",
            "userId": "user-001",
            "amount": 141.79,
            "status": "PENDING"
        }
    """;

    private final RestTemplate restTemplate = new RestTemplate();

    private final int THREAD_COUNT = 10;
    private final int OPERATIONS_PER_THREAD = 3000;

    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

    private final String BASE_URL = "http://localhost:%d/api/transactions";

    private final AtomicLong totalResponseTime = new AtomicLong(0);
    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicInteger createSuccessCount = new AtomicInteger(0);
    private final AtomicInteger createFailureCount = new AtomicInteger(0);
    private final AtomicInteger listSuccessCount = new AtomicInteger(0);
    private final AtomicInteger listFailureCount = new AtomicInteger(0);

    @Test
    void testCreateAndListOperations() throws InterruptedException {
        List<Future<?>> futures = new ArrayList<>();
        String baseUrl = String.format(BASE_URL, port);

        // 提交并发任务
        for (int i = 0; i < THREAD_COUNT; i++) {
            Future<?> future = executorService.submit(() -> {
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                    try {
                        long startTime = System.currentTimeMillis();
                        boolean success = performOperation(baseUrl, testJsonContent);
                        long endTime = System.currentTimeMillis();
                        
                        totalResponseTime.addAndGet(endTime - startTime);
                        totalRequests.incrementAndGet();
                        
                        if (!success) {
                            if (j % 2 == 0) {
                                createFailureCount.incrementAndGet();
                            } else {
                                listFailureCount.incrementAndGet();
                            }
                        } else {
                            if (j % 2 == 0) {
                                createSuccessCount.incrementAndGet();
                            } else {
                                listSuccessCount.incrementAndGet();
                            }
                        }
                        
                        // 添加小延迟，避免请求过于密集
                        Thread.sleep(1);
                    } catch (Exception e) {
                        if (j % 2 == 0) {
                            createFailureCount.incrementAndGet();
                        } else {
                            listFailureCount.incrementAndGet();
                        }
                    }
                }
            });
            futures.add(future);
        }

        // 等待所有任务完成
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                // 忽略执行异常
            }
        }

        // 输出测试结果
        System.out.println("Create and List API Stress Test Results:");
        System.out.println("Total Operations: " + (THREAD_COUNT * OPERATIONS_PER_THREAD));
        
        System.out.println("\nCreate Operation Results:");
        System.out.println("Total Create Operations: " + (THREAD_COUNT * OPERATIONS_PER_THREAD / 2));
        System.out.println("Successful Creates: " + createSuccessCount.get());
        System.out.println("Failed Creates: " + createFailureCount.get());
        System.out.println("Create Success Rate: " + 
            ((double) createSuccessCount.get() / (THREAD_COUNT * OPERATIONS_PER_THREAD / 2) * 100) + "%");
        
        System.out.println("\nList Operation Results:");
        System.out.println("Total List Operations: " + (THREAD_COUNT * OPERATIONS_PER_THREAD / 2));
        System.out.println("Successful Lists: " + listSuccessCount.get());
        System.out.println("Failed Lists: " + listFailureCount.get());
        System.out.println("List Success Rate: " + 
            ((double) listSuccessCount.get() / (THREAD_COUNT * OPERATIONS_PER_THREAD / 2) * 100) + "%");
        
        System.out.println("\nOverall Statistics:");
        System.out.println("Average Response Time: " + 
            ((double) totalResponseTime.get() / totalRequests.get()) + " ms");

        executorService.shutdown();
    }

    private boolean performOperation(String baseUrl, String testTransactionJson) {
        try {
            // 交替执行创建和列表操作
            if (ThreadLocalRandom.current().nextInt(2) == 0) {
                return createTransaction(baseUrl, testTransactionJson);
            } else {
                return listTransactions(baseUrl);
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean createTransaction(String baseUrl, String testTransactionJson) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(testTransactionJson, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);
            return response.getStatusCode().equals(HttpStatus.CREATED);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean listTransactions(String baseUrl) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);
            return response.getStatusCode().equals(HttpStatus.OK);
        } catch (Exception e) {
            return false;
        }
    }
} 