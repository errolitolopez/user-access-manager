package com.example.demo.audit.repository;

import com.example.demo.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the AuditLog entity.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Finds the IDs of the oldest audit log records.
     *
     * @param limit The maximum number of IDs to return.
     * @return A list of IDs of the oldest audit logs.
     */
    @Query(value = "SELECT al.id FROM AuditLog al ORDER BY al.dateCreated ASC LIMIT :limit")
    List<Long> findOldestAuditLogIds(int limit);
}
