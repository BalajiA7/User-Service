package org.example.user_service.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.example.user_service.exceptions.ExpiredTokenException;
import org.example.user_service.exceptions.InvalidTokenException;
import org.example.user_service.exceptions.PasswordMissMatchException;
import org.example.user_service.exceptions.UserNotFoundException;
import org.example.user_service.models.Token;
import org.example.user_service.models.User;
import org.example.user_service.repositories.TokenRepository;
import org.example.user_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TokenRepository tokenRepository, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User signup(String name, String email, String password) throws Exception {
        Optional<User> optionalUser = this.userRepository.findUserByEmail(email);
        if(optionalUser.isPresent()){
            throw new Exception("User already present");
        }

        // Store the Encoded password instead of the plain password
        String encodedPassword = this.bCryptPasswordEncoder.encode(password);

        // User Creation
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(encodedPassword);

        // Saving in DB
        return this.userRepository.save(user); //upsert
    }

    @Override
    public Token login(String email, String password) throws UserNotFoundException,PasswordMissMatchException {
        Optional<User> optionalUser = this.userRepository.findUserByEmail(email);
        User user = optionalUser.orElseThrow(()-> new UserNotFoundException("User not found"));
        boolean matches = this.bCryptPasswordEncoder.matches(password, user.getPassword());
        if(matches){
         //Issue a token
         String value = RandomStringUtils.randomAlphanumeric(128);
         Calendar c = Calendar.getInstance();
         c.add(Calendar.DATE, 30);
         Date thirtyDaysLater = c.getTime();

         Token token = new Token();
         token.setUser(user);
         token.setValue(value);
         token.setExpiresAt(thirtyDaysLater);
         token.setActive(true);
         return tokenRepository.save(token);
        }else{
            throw new PasswordMissMatchException("Password is incorrect");
        }
    }

    @Override
    public Token validateToken(String tokenValue) throws InvalidTokenException, ExpiredTokenException {
        /*
          1. Fetch token from db using value
          2. If token is not present in db, throw exception
          3. Else, check whether the token has expires or not
          4. if token is expired, then throw an exception
          5. else you are going to return the token
         */
        Optional<Token> tokenByValue = tokenRepository.findTokenByValue(tokenValue);
        Token token = tokenByValue.orElseThrow(() -> new InvalidTokenException("Please use a valid token"));
        Date expiresAt = token.getExpiresAt();
        Date now = new Date();
        // If now is greater than expires at
        if(now.after(expiresAt) || !token.isActive()){
            throw new ExpiredTokenException("Token is expired");
        }
        return token;
    }

    @Override
    public void logout(String tokenValue) throws Exception {
      /*
        1. Fetch token from db
        2. If the token is not present, then return 400
        3. Else set the is active to false and return
       */
        Optional<Token> tokenByValue = tokenRepository.findTokenByValue(tokenValue);
        Token token = tokenByValue.orElseThrow(() -> new InvalidTokenException("Please use a valid token"));

        if(token.isActive()){
            token.setActive(false);
            tokenRepository.save(token);
        }

    }
}
