package com.application.atm.errors;

// Non-editable Field exception.
public final class FieldException extends Exception {
    public FieldException(String message) {
        super(message);
    }

    public FieldException(String fieldName, String fieldValue) {
        super("Field " + fieldName + " is " + fieldValue + "! :( ");
    }
}
