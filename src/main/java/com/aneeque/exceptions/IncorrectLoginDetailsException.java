package com.aneeque.exceptions;

public class IncorrectLoginDetailsException extends Throwable {
    public IncorrectLoginDetailsException() {
    }

    public IncorrectLoginDetailsException(String message) {
        super(message);
    }

    public IncorrectLoginDetailsException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectLoginDetailsException(Throwable cause) {
        super(cause);
    }
}
