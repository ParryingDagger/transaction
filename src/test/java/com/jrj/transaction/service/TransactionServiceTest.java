package com.jrj.transaction.service;

import com.jrj.transaction.entity.Transaction;
import com.jrj.transaction.exception.TransactionNotFoundException;
import com.jrj.transaction.exception.TransactionDuplicatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionServiceTest {
    @Autowired
    private TransactionService transactionService;

    private Transaction testTransaction;
    
    @BeforeEach
    public void setUp() {
        testTransaction = new Transaction();
        testTransaction.setAccountId("my-account-001");
        testTransaction.setUserId("user-001");
        testTransaction.setAmount(new BigDecimal(141.79));
        testTransaction.setType("D");
        testTransaction.setStatus("PENDING");
    }

    @Test
    void testCreateTransaction() {
        Transaction createdTransaction = transactionService.create(testTransaction);
        assertNotNull(createdTransaction);
    }

    @Test
    void testCreateTransactionWithDuplicatedId() {
        transactionService.create(testTransaction);
        assertThrows(TransactionDuplicatedException.class, () -> {
            transactionService.create(testTransaction);
        });
    }

    @Test
    void testListAllTransactions() {
        transactionService.create(testTransaction);
        List<Transaction> transactions = transactionService.listAll();
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
    }

    @Test
    void testGetTransaction() {
        Transaction createdTransaction = transactionService.create(testTransaction);
        Transaction retrievedTransaction = transactionService.get(testTransaction.getId());
        assertNotNull(retrievedTransaction);
        assertEquals(createdTransaction.getId(), retrievedTransaction.getId());
    }

    @Test
    void testGetTransactionNotExist() {
        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.get("id-not-exist");
        });
    }

    @Test
    void testDeleteTransaction() {
        Transaction createdTransaction = transactionService.create(testTransaction);
        transactionService.delete(testTransaction.getId());
        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.get(createdTransaction.getId());
        });
    }

    @Test
    void testDeleteTransactionNotExist() {
        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.delete("id-not-exist");
        });
    }

    @Test
    void testUpdateTransaction() throws InterruptedException {
        Transaction createdTransaction = transactionService.create(testTransaction);
        Transaction newTransaction = new Transaction(
            "my-account-001",
            "user-001",
            new BigDecimal(99.99), 
            "D",
            "DONE"
        );
        Transaction updatedTransaction = transactionService.update(createdTransaction.getId(), newTransaction);
        assertNotNull(updatedTransaction);
        assertEquals(createdTransaction.getId(), updatedTransaction.getId());
        assertEquals(newTransaction.getAccountId(), updatedTransaction.getAccountId());
        assertEquals(newTransaction.getUserId(), updatedTransaction.getUserId());
        assertEquals(newTransaction.getAmount(), updatedTransaction.getAmount());
        assertEquals(newTransaction.getType(), updatedTransaction.getType());
        assertEquals(newTransaction.getStatus(), updatedTransaction.getStatus());
    }
}
