package com.jrj.transaction.exception;

public class TransactionDuplicatedException extends RuntimeException {
    public TransactionDuplicatedException() {
        super("Transaction duplicated");
    }

    public TransactionDuplicatedException(String message) {
        super(message);
    }
}
