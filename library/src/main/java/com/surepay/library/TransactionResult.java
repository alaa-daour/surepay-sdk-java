package com.surepay.library;

public class TransactionResult
{
    private String response;
    private boolean isSuccessfulTransaction;

    public TransactionResult() {}

    public TransactionResult(String response) {
        this.response = response;
    }

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isSuccessfulTransaction() {
        return this.isSuccessfulTransaction;
    }

    public void setSuccessfulTransaction(boolean successfulTransaction) {
        this.isSuccessfulTransaction = successfulTransaction;
    }
}