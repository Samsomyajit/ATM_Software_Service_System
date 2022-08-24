package com.application.atm.errors;

// Non-editable customer not found exception.
public final class CustomerNotFoundException extends Exception{
    public CustomerNotFoundException(long id) {
        super("Customer with id = " + id + " was not found! :(");
    }
}
