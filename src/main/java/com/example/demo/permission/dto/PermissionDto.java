package com.example.demo.permission.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for the Permission entity.
 */
public class PermissionDto {

    /**
     * The unique identifier for the permission.
     */
    private Long id;

    /**
     * The name of the permission.
     */
    private String name;

    /**
     * The date and time when the permission was created.
     */
    private LocalDateTime dateCreated;

    /**
     * The date and time when the permission was last updated.
     */
    private LocalDateTime dateUpdated;

    public PermissionDto() {
    }

    public PermissionDto(Long id, String name, LocalDateTime dateCreated, LocalDateTime dateUpdated) {
        this.id = id;
        this.name = name;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}
