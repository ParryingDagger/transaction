package com.jrj.transaction.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.List;

import com.jrj.transaction.entity.Transaction;
import com.jrj.transaction.entity.TransactionType;
import com.jrj.transaction.exception.TransactionNotFoundException;
import com.jrj.transaction.exception.TransactionDuplicatedException;
import com.jrj.transaction.exception.TransactionInvalidTypeException;
import com.jrj.transaction.service.TransactionService;
import com.jrj.transaction.service.TransactionServiceImpl;
import com.jrj.transaction.exception.GlobalExceptionHandler;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    private Transaction testTransaction;

    private String testJsonContent;
    private String testPartialUpdateContent;
    private String testInvalidTypeContent;
    
    @BeforeEach
    public void setUp() {
        testTransaction = new Transaction();
        testTransaction.setAccountId("my-account-001");
        testTransaction.setUserId("user-001");
        testTransaction.setAmount(new BigDecimal(141.79));
        testTransaction.setType(TransactionType.fromCode("D"));
        testTransaction.setStatus("PENDING");

        testJsonContent = """
        {
            "type": "D",
            "accountId": "my-account-001",
            "userId": "user-001",
            "amount": 141.79,
            "status": "PENDING"
        }
        """;

        testPartialUpdateContent = """
        {
            "status": "DONE"
        }
        """;

        testInvalidTypeContent = """
        {
            "type": "invalid_type",
            "accountId": "my-account-001",
            "userId": "user-001",
            "amount": 141.79,
            "status": "PENDING"
        }
        """;
    }

    @Test
    public void testCreateRequest() throws Exception {
        when(transactionService.create(any(Transaction.class))).thenReturn(testTransaction);

        mockMvc.perform(post("/api/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(testJsonContent))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.updatedAt").exists())
            .andExpect(jsonPath("$.amount").value(141.79))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.type").value("D"))
            .andExpect(jsonPath("$.accountId").value("my-account-001"))
            .andExpect(jsonPath("$.userId").value("user-001"));

        verify(transactionService, times(1)).create(any(Transaction.class));
    }

    @Test
    public void testCreateTransactionWithInvalidBody() throws Exception {
        mockMvc.perform(post("/api/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(testPartialUpdateContent))
            .andExpect(status().isBadRequest());

    }

    @Test
    public void testCreateTransactionWithDuplicatedId() throws Exception {
        TransactionDuplicatedException e = new TransactionDuplicatedException();
        when(transactionService.create(any(Transaction.class))).thenThrow(e);

        mockMvc.perform(post("/api/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(testJsonContent))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value(e.getMessage()));

        verify(transactionService, times(1)).create(any(Transaction.class));
    }

    @Test
    public void testCreateTransactionWithInvalidType() throws Exception {
        TransactionInvalidTypeException e = new TransactionInvalidTypeException("invalid_type");
        
        mockMvc.perform(post("/api/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(testInvalidTypeContent))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value(e.getMessage()));

    }

    @Test
    public void testListAllTransactions() throws Exception {
        when(transactionService.listAll()).thenReturn(List.of(testTransaction));

        mockMvc.perform(get("/api/transactions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").exists())
            .andExpect(jsonPath("$[0].createdAt").exists())
            .andExpect(jsonPath("$[0].updatedAt").exists())
            .andExpect(jsonPath("$[0].amount").value(141.79))
            .andExpect(jsonPath("$[0].status").value("PENDING"))
            .andExpect(jsonPath("$[0].type").value("D"))
            .andExpect(jsonPath("$[0].accountId").value("my-account-001"))
            .andExpect(jsonPath("$[0].userId").value("user-001"));

        verify(transactionService, times(1)).listAll();
    }

    @Test
    public void testListAllTransactionsEmpty() throws Exception {
        when(transactionService.listAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/transactions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        verify(transactionService, times(1)).listAll();
    }

    @Test
    public void testGetTransaction() throws Exception {
        when(transactionService.get(anyString())).thenReturn(testTransaction);

        mockMvc.perform(get("/api/transactions/{id}", testTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testTransaction.getId()))
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.updatedAt").exists())
            .andExpect(jsonPath("$.amount").value(141.79))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.type").value("D"))
            .andExpect(jsonPath("$.accountId").value("my-account-001"))
            .andExpect(jsonPath("$.userId").value("user-001"));

        verify(transactionService, times(1)).get(anyString());
    }

    @Test
    public void testGetTransactionNotFound() throws Exception {
        TransactionNotFoundException e = new TransactionNotFoundException();
        when(transactionService.get(anyString())).thenThrow(e);

        mockMvc.perform(get("/api/transactions/{id}", "non-existent-id"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value(e.getMessage()));
     
        verify(transactionService, times(1)).get(anyString());
    }

    @Test
    public void testDeleteTransaction() throws Exception {
        doNothing().when(transactionService).delete(anyString());

        mockMvc.perform(delete("/api/transactions/{id}", testTransaction.getId()))
            .andExpect(status().isNoContent());

        verify(transactionService, times(1)).delete(anyString());
    }

    @Test
    public void testDeleteTransactionNotFound() throws Exception {
        TransactionNotFoundException e = new TransactionNotFoundException();
        doThrow(e).when(transactionService).delete(anyString());

        mockMvc.perform(delete("/api/transactions/{id}", "non-existent-id"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value(e.getMessage()));
    
        verify(transactionService, times(1)).delete(anyString());
    }

    @Test
    public void testUpdateTransaction() throws Exception {
        when(transactionService.update(anyString(), any(Transaction.class))).thenReturn(testTransaction);

        mockMvc.perform(put("/api/transactions/{id}", testTransaction.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(testJsonContent))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testTransaction.getId()));
        
        verify(transactionService, times(1)).update(anyString(), any(Transaction.class));
    }

    @Test
    public void testUpdateTransactionNotFound() throws Exception {
        TransactionNotFoundException e = new TransactionNotFoundException();
        when(transactionService.update(anyString(), any(Transaction.class))).thenThrow(e);

        mockMvc.perform(put("/api/transactions/{id}", "non-existent-id")
            .contentType(MediaType.APPLICATION_JSON)
            .content(testJsonContent))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value(e.getMessage()));

        verify(transactionService, times(1)).update(anyString(), any(Transaction.class));
    }

    @Test
    public void testUpdateTransactionWithInvalidBody() throws Exception {
        mockMvc.perform(put("/api/transactions/{id}", testTransaction.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(testPartialUpdateContent))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateTransactionWithInvalidType() throws Exception {
        TransactionInvalidTypeException e = new TransactionInvalidTypeException("invalid_type");

        mockMvc.perform(put("/api/transactions/{id}", testTransaction.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(testInvalidTypeContent))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value(e.getMessage()));
    }

}
