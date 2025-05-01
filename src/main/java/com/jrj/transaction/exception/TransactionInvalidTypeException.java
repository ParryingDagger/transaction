package com.jrj.transaction.exception;

public class TransactionInvalidTypeException extends RuntimeException {
    public TransactionInvalidTypeException(String code) {
        super("Transaction type is invalid with code: " + code);
    }

}
