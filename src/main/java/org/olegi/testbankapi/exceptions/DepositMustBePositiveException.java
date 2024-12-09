package org.olegi.testbankapi.exceptions;

public class DepositMustBePositiveException extends RuntimeException{
    public DepositMustBePositiveException(String message) {
        super(message);
    }
}
