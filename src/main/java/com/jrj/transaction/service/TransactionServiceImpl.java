package com.jrj.transaction.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import com.jrj.transaction.entity.Transaction;
import com.jrj.transaction.exception.TransactionNotFoundException;
import com.jrj.transaction.exception.TransactionDuplicatedException;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {
    /**
     * 根据需求，先简单使用内存存储
     * 如有必要改造为 dao 层
     * 
     * 由于直接内存存储，暂不增加缓存
     */
    private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();

    @Override
    public Transaction create(Transaction transaction) {
        if (transactions.containsKey(transaction.getId())) {
            throw new TransactionDuplicatedException();
        }
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    @Override
    public List<Transaction> listAll() {
        return new ArrayList<>(transactions.values());
    }

    @Override
    public Transaction get(String id) {
        Transaction transaction = transactions.get(id);
        if (transaction == null) {
            throw new TransactionNotFoundException();
        }
        return transaction;
    }

    @Override
    public void delete(String id) {
        Transaction removed = transactions.remove(id);
        if (removed == null) {
            throw new TransactionNotFoundException();
        }
    }

    @Override
    public Transaction update(String id, Transaction newTransaction) {
        Transaction transaction = transactions.get(id);
        if (transaction == null) {
            throw new TransactionNotFoundException();
        }
        transaction.setUpdatedAt(LocalDateTime.now());
        transaction.setUserId(newTransaction.getUserId() != null ? newTransaction.getUserId() : transaction.getUserId());
        transaction.setAccountId(newTransaction.getAccountId() != null ? newTransaction.getAccountId() : transaction.getAccountId());
        transaction.setAmount(newTransaction.getAmount() != null ? newTransaction.getAmount() : transaction.getAmount());
        transaction.setStatus(newTransaction.getStatus() != null ? newTransaction.getStatus() : transaction.getStatus());
        transaction.setType(newTransaction.getType() != null ? newTransaction.getType() : transaction.getType());
        transactions.put(id, transaction);
        return transaction;
    }
}
