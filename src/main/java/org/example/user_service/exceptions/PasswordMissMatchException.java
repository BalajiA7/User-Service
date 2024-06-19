package org.example.user_service.exceptions;

public class PasswordMissMatchException extends Exception{
    public PasswordMissMatchException(String message){
        super(message);
    }
}
