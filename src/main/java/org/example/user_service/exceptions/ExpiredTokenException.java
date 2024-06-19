package org.example.user_service.exceptions;

public class ExpiredTokenException extends Exception {
    public ExpiredTokenException(String tokenIsExpired) {
        super(tokenIsExpired);
    }
}
