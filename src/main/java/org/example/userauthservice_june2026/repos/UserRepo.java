package org.example.userauthservice_june2026.repos;

import org.example.userauthservice_june2026.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository   //This is optional here
public interface UserRepo extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);
}
