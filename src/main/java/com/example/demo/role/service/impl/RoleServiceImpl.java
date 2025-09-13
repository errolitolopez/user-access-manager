package com.example.demo.role.service.impl;

import com.example.demo.audit.event.AuditLogEvent;
import com.example.demo.exception.InvalidInputException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.permission.entity.Permission;
import com.example.demo.permission.repository.PermissionRepository;
import com.example.demo.role.dto.RoleDto;
import com.example.demo.role.entity.Role;
import com.example.demo.role.mapper.RoleMapper;
import com.example.demo.role.repository.RoleRepository;
import com.example.demo.role.service.RoleService;
import com.example.demo.security.service.IdentityService;
import com.example.demo.util.constants.ValidationMessages;
import com.example.demo.util.enums.EventType;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;
    private final IdentityService identityService;
    private final ApplicationEventPublisher eventPublisher;

    public RoleServiceImpl(RoleRepository roleRepository,
                           PermissionRepository permissionRepository,
                           RoleMapper roleMapper,
                           IdentityService identityService,
                           ApplicationEventPublisher eventPublisher) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.roleMapper = roleMapper;
        this.identityService = identityService;
        this.eventPublisher = eventPublisher;
    }


    @Override
    @Transactional
    public void addPermissionToRole(Long permissionId, Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with ID: " + permissionId));

        role.getPermissions().add(permission);
        roleRepository.save(role);

        // Publish the event
        String username = identityService.getAuthenticatedUsername();
        Map<String, Object> details = new HashMap<>();
        details.put("roleId", role.getId());
        details.put("roleName", role.getName());
        details.put("permissionId", permission.getId());
        details.put("permissionName", permission.getName());
        eventPublisher.publishEvent(new AuditLogEvent(this, username, null, EventType.ASSIGN_PERMISSION_TO_ROLE, details));
    }

    @Override
    @Transactional
    public RoleDto create(String name) {
        roleRepository.findByName(name).ifPresent(r -> {
            throw new InvalidInputException(ValidationMessages.NAME_ALREADY_EXIST);
        });
        Role newRole = new Role();
        newRole.setName(name);
        Role savedRole = roleRepository.save(newRole);

        // Publish the event
        String username = identityService.getAuthenticatedUsername();
        Map<String, Object> details = new HashMap<>();
        details.put("roleId", savedRole.getId());
        details.put("roleName", savedRole.getName());
        eventPublisher.publishEvent(new AuditLogEvent(this, username, null, EventType.CREATE_ROLE, details));

        return roleMapper.toDto(savedRole);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<RoleDto> getAll(RoleDto roleDto, Pageable pageable) {
        Role roleExample = roleMapper.toEntity(roleDto);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<Role> example = Example.of(roleExample, matcher);

        return roleRepository.findAll(example, pageable)
                .map(roleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDto getById(Long id) {
        return roleRepository.findById(id).map(roleMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));
    }

    @Override
    @Transactional
    public void removePermissionFromRole(Long permissionId, Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with ID: " + permissionId));

        role.getPermissions().remove(permission);
        roleRepository.save(role);

        // Publish the event
        String username = identityService.getAuthenticatedUsername();
        Map<String, Object> details = new HashMap<>();
        details.put("roleId", role.getId());
        details.put("roleName", role.getName());
        details.put("permissionId", permission.getId());
        details.put("permissionName", permission.getName());
        eventPublisher.publishEvent(new AuditLogEvent(this, username, null, EventType.REMOVE_PERMISSION_FROM_ROLE, details));
    }
}
