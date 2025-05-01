package com.jrj.transaction.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.jrj.transaction.exception.TransactionInvalidTypeException;

public enum TransactionType {
    DEPOSIT("D", "deposit"),
    WITHDRAWAL("W", "withdrawal");

    private String code;
    private String description;

    TransactionType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static TransactionType fromCode(String code) {
        for (TransactionType type: TransactionType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new TransactionInvalidTypeException(code);
    }
}