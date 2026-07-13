package org.example.userauthservice_june2026.controllers;

import org.example.userauthservice_june2026.dtos.LoginRequestDto;
import org.example.userauthservice_june2026.dtos.SignupRequestDto;
import org.example.userauthservice_june2026.dtos.UserDto;
import org.example.userauthservice_june2026.models.User;
import org.example.userauthservice_june2026.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    //signup
    //login

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupRequestDto signupRequestDto) {
        try {
           User user = authService.signup(signupRequestDto.getName(), signupRequestDto.getEmail(), signupRequestDto.getPassword(), signupRequestDto.getPhoneNumber());
           UserDto userDto = from(user);
           return new ResponseEntity<>(userDto, HttpStatus.CREATED);
        } catch (RuntimeException exception) {
            throw exception;
        }
    }


    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            User user = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            UserDto userDto = from(user);
            return new ResponseEntity<>(userDto,HttpStatus.OK);
        } catch (RuntimeException exception) {
            throw exception;
        }
    }

    private UserDto from(User user) {

    }
}
