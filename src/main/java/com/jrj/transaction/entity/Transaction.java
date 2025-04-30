package com.jrj.transaction.entity;

import java.util.UUID;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
 * 交易实体类
 * 先兼做DTO，有必要再分离
 */
public class Transaction {
    /**
     * 先简单用 id 做唯一标识，用于判断是否重复
     */
    private String id;

    @NotNull(message = "userId is required")
    private String userId;

    @NotNull(message = "accountId is required")
    private String accountId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.00", message = "amount must be greater than 0")
    private BigDecimal amount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @NotNull(message = "status is required")
    private String status;

    @NotNull(message = "type is required")
    private String type;

    public Transaction() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Transaction(String userId, String accountId, BigDecimal amount, String status, String type) {
        this();
        this.userId = userId;
        this.accountId = accountId;
        this.amount = amount;
        this.status = status;
        this.type = type;
    }
    
    public Transaction(String id, LocalDateTime createdAt, LocalDateTime updatedAt, String userId, String accountId, BigDecimal amount, String status, String type) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.accountId = accountId;
        this.amount = amount;
        this.status = status;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
