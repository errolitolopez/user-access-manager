package com.example.demo.mail.controller;

import com.example.demo.mail.dto.CreateSmtpConfigDto;
import com.example.demo.mail.dto.SmtpConfigDto;
import com.example.demo.mail.service.SmtpConfigService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing SMTP configurations.
 */
@RestController
@RequestMapping("/api/smtp-configs")
@Validated
public class SmtpConfigController {

    private final SmtpConfigService smtpConfigService;

    public SmtpConfigController(SmtpConfigService smtpConfigService) {
        this.smtpConfigService = smtpConfigService;
    }

    /**
     * Creates a new SMTP configuration.
     *
     * @param createSmtpConfigDto The DTO containing the SMTP configuration data.
     * @return A ResponseEntity with the created SmtpConfigDto.
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_SMTP_CONFIGS')")
    public ResponseEntity<SmtpConfigDto> create(@Valid @RequestBody CreateSmtpConfigDto createSmtpConfigDto) {
        SmtpConfigDto createdConfig = smtpConfigService.create(createSmtpConfigDto);
        return new ResponseEntity<>(createdConfig, HttpStatus.CREATED);
    }

    /**
     * Updates an existing SMTP configuration.
     *
     * @param smtpConfigDto The DTO with the updated SMTP configuration data.
     * @return A response entity indicating success.
     */
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_SMTP_CONFIGS')")
    public ResponseEntity<SmtpConfigDto> update(@Valid @RequestBody SmtpConfigDto smtpConfigDto) {
        SmtpConfigDto updatedConfig = smtpConfigService.update(smtpConfigDto);
        return ResponseEntity.ok(updatedConfig);
    }

    /**
     * Deletes an SMTP configuration by ID.
     *
     * @param id The ID of the configuration to delete.
     * @return A response entity indicating success.
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_SMTP_CONFIGS')")
    public ResponseEntity<Void> delete(@RequestParam @NotNull Long id) {
        smtpConfigService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves an SMTP configuration by its ID.
     *
     * @param id The ID of the configuration to retrieve.
     * @return A ResponseEntity with the found SmtpConfigDto.
     */
    @GetMapping("/get")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('READ_SMTP_CONFIGS')")
    public ResponseEntity<SmtpConfigDto> getById(@RequestParam @NotNull Long id) {
        SmtpConfigDto configDto = smtpConfigService.getById(id);
        return ResponseEntity.ok(configDto);
    }

    /**
     * Retrieves a paginated list of all SMTP configurations based on a search example.
     *
     * @param smtpConfigDto The DTO to use as an example for the search.
     * @param pageable      Pagination and sorting information.
     * @return A page of SmtpConfigDto.
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('READ_SMTP_CONFIGS')")
    public ResponseEntity<Page<SmtpConfigDto>> getAll(SmtpConfigDto smtpConfigDto, Pageable pageable) {
        Page<SmtpConfigDto> configs = smtpConfigService.getAll(smtpConfigDto, pageable);
        return ResponseEntity.ok(configs);
    }
}
