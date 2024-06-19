package org.example.user_service.services;

import org.example.user_service.exceptions.ExpiredTokenException;
import org.example.user_service.exceptions.InvalidTokenException;
import org.example.user_service.models.Token;
import org.example.user_service.models.User;

public interface UserService {
    public User signup(String name, String email, String password) throws Exception;
    public Token login(String email, String password) throws Exception;
    public Token validateToken(String tokenValue) throws InvalidTokenException, ExpiredTokenException;
    public void logout(String token) throws Exception;
}
