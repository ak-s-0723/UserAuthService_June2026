package org.example.userauthservice_june2026.services;

import org.example.userauthservice_june2026.models.Role;
import org.example.userauthservice_june2026.models.Status;
import org.example.userauthservice_june2026.models.User;
import org.example.userauthservice_june2026.repos.RoleRepo;
import org.example.userauthservice_june2026.repos.UserRepo;
import org.example.userauthservice_june2026.constants.RoleValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

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

    public User login(String email,String password) {
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

        return user;
    }
}
