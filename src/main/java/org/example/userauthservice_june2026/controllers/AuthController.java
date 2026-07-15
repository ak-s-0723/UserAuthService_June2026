package org.example.userauthservice_june2026.controllers;

import org.antlr.v4.runtime.misc.Pair;
import org.example.userauthservice_june2026.dtos.LoginRequestDto;
import org.example.userauthservice_june2026.dtos.SignupRequestDto;
import org.example.userauthservice_june2026.dtos.UserDto;
import org.example.userauthservice_june2026.dtos.ValidateTokenRequestDto;
import org.example.userauthservice_june2026.models.Role;
import org.example.userauthservice_june2026.models.User;
import org.example.userauthservice_june2026.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            Pair<User,String> response = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            User user = response.a;
            String token = response.b;
            UserDto userDto = from(user);
            MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
            Map<String,String> dummy = new HashMap<>();
            headers.add(HttpHeaders.SET_COOKIE,"auth_session_id = "+token);
            return new ResponseEntity<>(userDto,headers,HttpStatus.OK);
        } catch (RuntimeException exception) {
            throw exception;
        }
    }

    @PostMapping("/validateToken")
    public Boolean validateToken(@RequestBody ValidateTokenRequestDto validateTokenRequestDto)
    {
        return authService.validateToken(validateTokenRequestDto.getToken());
    }

    private UserDto from(User user) {
      UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setId(user.getId());
        List<String> roles = new ArrayList<>();
        for(Role role : user.getRoles()) {
            roles.add(role.getValue());
        }
        userDto.setRoles(roles);
        return userDto;
    }
}
