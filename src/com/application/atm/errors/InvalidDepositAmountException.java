package com.application.atm.errors;

// Non-editable customer not found exception.
 final public class InvalidDepositAmountException extends Exception{
    public InvalidDepositAmountException() {
        super("Invalid deposit amount! :(");
    }

    public InvalidDepositAmountException(String message) {
        super(message);
    }
}
