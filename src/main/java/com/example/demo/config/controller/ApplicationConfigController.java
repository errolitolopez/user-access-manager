package com.example.demo.config.controller;

import com.example.demo.config.dto.ApplicationConfigDto;
import com.example.demo.config.service.ApplicationConfigService;
import com.example.demo.controller.BaseController;
import com.example.demo.security.dto.ApiResponse;
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
 * REST controller for managing application configurations.
 */
@RestController
@RequestMapping("/api/configs")
@Validated
public class ApplicationConfigController extends BaseController {

    private final ApplicationConfigService applicationConfigService;

    public ApplicationConfigController(ApplicationConfigService applicationConfigService) {
        this.applicationConfigService = applicationConfigService;
    }

    /**
     * Creates a new application configuration.
     *
     * @param configDto The DTO containing the key, value, and enabled status.
     * @return A ResponseEntity with the created ApplicationConfigDto.
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_CONFIG')")
    public ResponseEntity<ApiResponse<ApplicationConfigDto>> create(@Valid @RequestBody ApplicationConfigDto configDto) {
        ApplicationConfigDto createdConfig = applicationConfigService.create(configDto);
        return buildSuccessResponse(HttpStatus.CREATED, "Configuration created successfully.", createdConfig);
    }

    /**
     * Updates an existing application configuration.
     *
     * @param configDto The DTO with the updated values.
     * @return A response entity indicating success.
     */
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_CONFIG')")
    public ResponseEntity<ApiResponse<ApplicationConfigDto>> update(@Valid @RequestBody ApplicationConfigDto configDto) {
        ApplicationConfigDto updatedConfig = applicationConfigService.update(configDto);
        return buildSuccessResponse(HttpStatus.OK, "Configuration updated successfully.", updatedConfig);
    }

    /**
     * Deletes an application configuration by ID.
     *
     * @param id The ID of the configuration to delete.
     * @return A response entity indicating success.
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_CONFIG')")
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam @NotNull Long id) {
        applicationConfigService.delete(id);
        return buildSuccessResponse(HttpStatus.NO_CONTENT, "Configuration deleted successfully.");
    }

    /**
     * Toggles the enabled status of an application configuration.
     *
     * @param id The ID of the configuration to update.
     * @param enabled The new enabled status.
     * @return A response entity indicating success.
     */
    @PutMapping("/toggle-enabled")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_CONFIG')")
    public ResponseEntity<ApiResponse<Void>> toggleEnabledStatus(@RequestParam @NotNull Long id, @RequestParam @NotNull Boolean enabled) {
        applicationConfigService.updateEnabledStatus(id, enabled);
        return buildSuccessResponse(HttpStatus.OK, "Configuration enabled status updated successfully.");
    }

    /**
     * Retrieves a paginated list of configurations based on an example DTO.
     *
     * @param configDto The DTO to use as an example for the search.
     * @param pageable  Pagination and sorting information.
     * @return A page of ApplicationConfigDto.
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('READ_CONFIG')")
    public ResponseEntity<ApiResponse<Page<ApplicationConfigDto>>> getAll(ApplicationConfigDto configDto, Pageable pageable) {
        Page<ApplicationConfigDto> configs = applicationConfigService.getAll(configDto, pageable);
        return buildSuccessResponse(HttpStatus.OK, "Configurations retrieved successfully.", configs);
    }
}
