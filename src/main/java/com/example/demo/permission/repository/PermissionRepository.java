package com.example.demo.permission.repository;

import com.example.demo.permission.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the Permission entity.
 * Provides standard CRUD operations.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Finds a Permission entity by its name.
     *
     * @param name The permission name to search for.
     * @return An Optional containing the found Permission, or empty if not found.
     */
    Optional<Permission> findByName(String name);
}
