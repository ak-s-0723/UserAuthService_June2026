package org.example.userauthservice_june2026.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.antlr.v4.runtime.misc.Pair;
import org.example.userauthservice_june2026.models.Role;
import org.example.userauthservice_june2026.models.Status;
import org.example.userauthservice_june2026.models.User;
import org.example.userauthservice_june2026.models.UserSession;
import org.example.userauthservice_june2026.repos.RoleRepo;
import org.example.userauthservice_june2026.repos.SessionRepo;
import org.example.userauthservice_june2026.repos.UserRepo;
import org.example.userauthservice_june2026.constants.RoleValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private SecretKey secretKey;

    public User signup(String name,String email,String password, String phoneNumber) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if (userOptional.isPresent()) {
            throw new RuntimeException("User Exists !! Please try with different email Id");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setPhoneNumber(phoneNumber);

        Role role = null;
        Optional<Role> roleOptional = roleRepo.findByValue(RoleValues.NON_ADMIN);
        if (roleOptional.isPresent()) {
            role = roleOptional.get();
        } else {
            role = new Role();
            role.setValue(RoleValues.NON_ADMIN);
            roleRepo.save(role);
        }

        List<Role> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);

        return userRepo.save(user);
    }

    public Pair<User,String> login(String email, String password) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Please signup first");
        }

        User user = userOptional.get();
        if (user.getStatus().equals(Status.INACTIVE)) {
            throw new RuntimeException("Account suspended, Please try with another email Id");
        }

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid Credentials");
        }

        //JWT Generation logic
        //Hardcoded payload just for demonstration
//        String message = "{\n" +
//                "   \"email\": \"anurag@gmail.com\",\n" +
//                "   \"roles\": [\n" +
//                "      \"instructor\",\n" +
//                "      \"buddy\"\n" +
//                "   ],\n" +
//                "   \"expirationDate\": \"2ndApril2026\"\n" +
//                "}";
//
//        byte[] content = message.getBytes(StandardCharsets.UTF_8);

        Map<String,Object> claims = new HashMap<>();
        claims.put("user_id",user.getId());

        List<String> roleValues = new ArrayList<>();
        for (Role role : user.getRoles()) {
            roleValues.add(role.getValue());
        }

        claims.put("access",roleValues);
        Long currentTimeInMillis = System.currentTimeMillis();
        claims.put("iat",currentTimeInMillis); //iat = issued at in ms
        claims.put("exp",currentTimeInMillis+100000); //exp = expiry in ms
        claims.put("issued_by","scaler");
        claims.put("type","auth");


        //algorithm and secret key for signature generation
        //in AuthConfig.java


        //Generating token on basis of payload only (which is content byte-array)
        //String token = Jwts.builder().content(content).compact();

        //Generating token using payload and then signing it with secret key
        //String token = Jwts.builder().content(content).signWith(secretKey).compact();

        //generating a JWT on basis of actual user claims
        String token = Jwts.builder().claims(claims).signWith(secretKey).compact();

        //Assuming session will not exist before
        UserSession userSession = new UserSession();
        userSession.setStatus(Status.ACTIVE);
        userSession.setUser(user);
        userSession.setToken(token);
        sessionRepo.save(userSession);

        return new Pair<>(user,token);
    }

    public Boolean validateToken(String token) {
        Optional<UserSession> optionalUserSession = sessionRepo.findByToken(token);
        if (optionalUserSession.isEmpty()) {
            return false;
        }

        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();

        Claims claims = jwtParser.parseSignedClaims(token).getPayload();
        Long expiry = (Long)claims.get("exp");

        Long currentTime = System.currentTimeMillis();
        System.out.println("expiry = "+expiry);
        System.out.println("currentTime = "+currentTime);
        if(currentTime > expiry) {
            System.out.println("Token has expired");
            sessionRepo.delete(optionalUserSession.get());
            return false;
        }

        return true;
    }
}
