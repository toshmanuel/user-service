package com.aneeque.exceptions;

public class AccountNotVerifiedException extends Throwable {
    public AccountNotVerifiedException() {
    }

    public AccountNotVerifiedException(String message) {
        super(message);
    }

    public AccountNotVerifiedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountNotVerifiedException(Throwable cause) {
        super(cause);
    }
}
