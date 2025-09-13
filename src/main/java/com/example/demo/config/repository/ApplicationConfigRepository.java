package com.example.demo.config.repository;

import com.example.demo.config.entity.ApplicationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the ApplicationConfig entity.
 * Provides standard CRUD operations and custom query methods.
 */
@Repository
public interface ApplicationConfigRepository extends JpaRepository<ApplicationConfig, Long> {

    /**
     * Finds a configuration entry by its unique key.
     *
     * @param configKey The key of the configuration to search for.
     * @return An Optional containing the found ApplicationConfig, or empty if not found.
     */
    Optional<ApplicationConfig> findByConfigKey(String configKey);


    /**
     * Finds all configuration settings based on their enabled status.
     *
     * @param enabled A boolean flag to filter by the enabled status.
     * @return A list of {@link ApplicationConfig} entities that match the criteria.
     */
    List<ApplicationConfig> findAllByEnabled(Boolean enabled);
}
