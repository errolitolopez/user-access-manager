package com.example.demo.mail.repository;

import com.example.demo.mail.entity.SmtpConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the SmtpConfig entity.
 * Provides standard CRUD operations and custom query methods.
 */
@Repository
public interface SmtpConfigRepository extends JpaRepository<SmtpConfig, Long> {

    /**
     * Finds an SMTP configuration by its unique name.
     *
     * @param name The name of the configuration to search for.
     * @return An Optional containing the found SmtpConfig, or empty if not found.
     */
    Optional<SmtpConfig> findByName(String name);

    /**
     * Finds all SMTP configurations based on their enabled status.
     *
     * @param enabled A boolean flag to filter by the enabled status.
     * @return A list of {@link SmtpConfig} entities that match the criteria.
     */
    List<SmtpConfig> findAllByEnabled(Boolean enabled);
}
