package com.odc.hub.admin.controller;

import com.odc.hub.audit.dto.AuditDTO;
import com.odc.hub.audit.mapper.AuditMapper;
import com.odc.hub.audit.model.AuditLog;
import com.odc.hub.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/admin/audit")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuditController {

    private final AuditLogRepository auditLogRepository;

    public AdminAuditController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    public Page<AuditDTO> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return auditLogRepository.findAll(pageable)
                .map(AuditMapper::toDto);
    }

    @GetMapping("/search")
    public Page<AuditDTO> searchAudit(
            @RequestParam(required = false) String actorEmail,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<AuditLog> logs;

        if (actorEmail != null) {
            logs = auditLogRepository.findByActorEmail(actorEmail, pageable);
        } else if (action != null) {
            logs = auditLogRepository.findByAction(action, pageable);
        } else if (from != null && to != null) {
            logs = auditLogRepository.findByCreatedAtBetween(from, to, pageable);
        } else {
            logs = auditLogRepository.findAll(pageable);
        }

        return logs.map(AuditMapper::toDto);
    }

}
