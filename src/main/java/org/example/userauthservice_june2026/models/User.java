package org.example.userauthservice_june2026.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
public class User extends BaseModel {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    @ManyToMany
    private List<Role> roles = new ArrayList<>();
}
//
//1            M
//user         role
//M             1
//
//M      :     M