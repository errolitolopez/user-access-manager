package com.example.demo.user.mapper;

import com.example.demo.role.mapper.RoleMapper;
import com.example.demo.user.dto.CreateUserDto;
import com.example.demo.user.dto.UserDto;
import com.example.demo.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper interface for converting between User entity and User DTO.
 * This class uses MapStruct to provide efficient and clean mapping.
 */
@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    /**
     * Converts a User entity to a UserDto.
     *
     * @param user The User entity.
     * @return The corresponding UserDto.
     */
    UserDto toDto(User user);

    /**
     * Converts a list of User entities to a list of UserDto.
     *
     * @param users The list of User entities.
     * @return A list of UserDto.
     */
    List<UserDto> toDtoList(List<User> users);

    /**
     * Converts a CreateUserDto to a User entity.
     *
     * @param createUserDto The CreateUserDto.
     * @return The corresponding User entity.
     */
    @Mapping(target = "id", ignore = true)
    User toEntity(CreateUserDto createUserDto);

    /**
     * Converts a UserDto to a User entity.
     *
     * @param userDto The UserDto.
     * @return The corresponding User entity.
     */
    @Mapping(target = "roles", ignore = true)
    User toEntity(UserDto userDto);
}
