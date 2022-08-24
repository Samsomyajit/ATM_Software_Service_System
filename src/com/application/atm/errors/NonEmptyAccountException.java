package com.application.atm.errors;

// Non-editable customer not found exception.
public final class NonEmptyAccountException extends Exception{
    public NonEmptyAccountException() {
        super("Cannot close account! :(\nAccount is not empty.");
    }
}
