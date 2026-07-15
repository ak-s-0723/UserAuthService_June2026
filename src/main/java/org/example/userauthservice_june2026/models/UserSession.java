package org.example.userauthservice_june2026.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class UserSession extends BaseModel {
    private String token;

    @ManyToOne
    private User user;
}


//1           1
//session     user
// M            1
//
//
//m   :  1

