package org.example.userauthservice_june2026.repos;

import org.example.userauthservice_june2026.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role,Long> {
    Optional<Role> findByValue(String value);
}
