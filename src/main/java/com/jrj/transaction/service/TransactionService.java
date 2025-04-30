package com.jrj.transaction.service;

import java.util.List;

import com.jrj.transaction.entity.Transaction;

public interface TransactionService {
    Transaction create(Transaction transaction);
    List<Transaction> listAll();
    Transaction get(String id);
    void delete(String id);
    Transaction update(String id, Transaction transaction);
} 
