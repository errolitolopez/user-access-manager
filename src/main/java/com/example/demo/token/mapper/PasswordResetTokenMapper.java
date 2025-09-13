package com.example.demo.token.mapper;

import com.example.demo.token.dto.PasswordResetTokenDto;
import com.example.demo.token.entity.PasswordResetToken;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper interface for converting between PasswordResetToken entity and DTO.
 */
@Mapper(componentModel = "spring")
public interface PasswordResetTokenMapper {

    /**
     * Converts a PasswordResetToken entity to a PasswordResetTokenDto.
     * @param passwordResetToken The PasswordResetToken entity.
     * @return The corresponding DTO.
     */
    PasswordResetTokenDto toDto(PasswordResetToken passwordResetToken);

    /**
     * Converts a list of PasswordResetToken entities to a list of DTOs.
     * @param passwordResetTokens The list of PasswordResetToken entities.
     * @return A list of PasswordResetTokenDto.
     */
    List<PasswordResetTokenDto> toDtoList(List<PasswordResetToken> passwordResetTokens);
}
