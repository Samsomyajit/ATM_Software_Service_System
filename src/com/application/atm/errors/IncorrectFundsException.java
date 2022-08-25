package com.application.atm.errors;

// Non-editable customer not found exception.
public final class IncorrectFundsException extends Exception {
    public IncorrectFundsException() {
        super("Zero balance cannot be withdrawn! :(");
    }

    public IncorrectFundsException(String message) {
        super(message);
    }
}
