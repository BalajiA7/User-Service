package org.example.user_service.controllers;

import org.example.user_service.dtos.LoginRequestDto;
import org.example.user_service.dtos.LogoutRequestDTO;
import org.example.user_service.dtos.SignupRequestDto;
import org.example.user_service.dtos.ValidateTokenRequestDto;
import org.example.user_service.exceptions.ExpiredTokenException;
import org.example.user_service.exceptions.InvalidTokenException;
import org.example.user_service.models.Token;
import org.example.user_service.models.User;
import org.example.user_service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService  = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody SignupRequestDto requestDto){
        try{
            //TODO Basic Validation
            System.out.println(requestDto);
            User user = userService.signup(requestDto.getName(), requestDto.getEmail(), requestDto.getPassword());
            return new ResponseEntity<>(user, HttpStatusCode.valueOf(201));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Token> login(@RequestBody LoginRequestDto requestDto){
       try{
           //TODO Basic Validation
           Token token = userService.login(requestDto.getEmail(), requestDto.getPassword());
           return new ResponseEntity<>(token, HttpStatusCode.valueOf(200));
       }catch (Exception e){
            return new ResponseEntity<>(null,HttpStatusCode.valueOf(400));
       }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDTO requestDto){
       try {
          //TODO Basic Validation
          userService.logout(requestDto.getToken());
          return new ResponseEntity<>(HttpStatus.OK);
       }catch (Exception e){
           return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
       }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<Token> validateToken(@RequestBody ValidateTokenRequestDto requestDto){
        try{
            //TODO Basic Validation
            Token token = userService.validateToken(requestDto.getToken());
            return new ResponseEntity<>(token, HttpStatusCode.valueOf(200));
        }catch (ExpiredTokenException ete){
            return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        }catch (InvalidTokenException ite){
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
    }
}
