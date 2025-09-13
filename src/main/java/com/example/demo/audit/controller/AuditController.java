package com.example.demo.audit.controller;

import com.example.demo.audit.dto.AuditLogDto;
import com.example.demo.audit.dto.QueryAuditLogDto;
import com.example.demo.audit.service.AuditService;
import com.example.demo.controller.BaseController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for retrieving and managing the audit log.
 */
@RestController
@RequestMapping("/api/audit")
@Validated
public class AuditController extends BaseController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Retrieves a paginated list of audit logs.
     *
     * @param queryAuditLogDto A DTO containing fields to filter the search.
     * @param pageable         Pagination and sorting information provided by Spring Data.
     * @return A {@link ResponseEntity} containing a page of {@link AuditLogDto}.
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('READ_AUDIT_LOGS')")
    public ResponseEntity<?> search(QueryAuditLogDto queryAuditLogDto, Pageable pageable) {
        Page<AuditLogDto> auditLogs = auditService.getAll(queryAuditLogDto, pageable);
        return buildSuccessResponse(HttpStatus.OK, "Audit logs retrieved successfully.", auditLogs);
    }
}
