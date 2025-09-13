package com.example.demo.permission.service.impl;

import com.example.demo.permission.dto.PermissionDto;
import com.example.demo.permission.entity.Permission;
import com.example.demo.exception.InvalidInputException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.permission.mapper.PermissionMapper;
import com.example.demo.permission.repository.PermissionRepository;
import com.example.demo.permission.service.PermissionService;
import com.example.demo.util.constants.ValidationMessages;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    public PermissionServiceImpl(PermissionRepository permissionRepository, PermissionMapper permissionMapper) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
    }

    @Override
    @Transactional
    public PermissionDto create(String name) {
        permissionRepository.findByName(name).ifPresent(p -> {
            throw new InvalidInputException(ValidationMessages.NAME_ALREADY_EXIST);
        });

        Permission newPermission = new Permission();
        newPermission.setName(name);

        Permission savedPermission = permissionRepository.save(newPermission);
        return permissionMapper.toDto(savedPermission);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Permission not found with ID: " + id);
        }
        permissionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionDto> getAll(PermissionDto permissionDto, Pageable pageable) {
        Permission permissionExample = permissionMapper.toEntity(permissionDto);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<Permission> example = Example.of(permissionExample, matcher);

        return permissionRepository.findAll(example, pageable)
                .map(permissionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionDto getById(Long id) {
        Optional<Permission> permission = permissionRepository.findById(id);
        return permission.map(permissionMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with ID: " + id));
    }
}
