package com.application.atm.errors;

// Non-editable customer not found exception.
public class pinMissMatchException extends Exception {
    public pinMissMatchException() {
        super("Wrong pin! :|");
    }
}
