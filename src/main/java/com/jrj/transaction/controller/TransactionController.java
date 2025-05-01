package com.jrj.transaction.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.jrj.transaction.entity.Transaction;
import com.jrj.transaction.service.TransactionService;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction) {
        Transaction createdTransaction = transactionService.create(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    /**
     * 当前根据要求只提供 listAll 接口
     * TODO: listTransactions 支持排序，过滤分页
     * @return
     */
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.listAll();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable String id) {
        Transaction transaction = transactionService.get(id);
        return ResponseEntity.ok(transaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable String id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 完整更新，除不可变属性之外
     * @param id
     * @param transaction
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable String id, @Valid @RequestBody Transaction transaction) {
        Transaction updated = transactionService.update(id, transaction);
        return ResponseEntity.ok(updated);
    }

}
