package com.eLearning.dto;

import com.eLearning.enums.UserRole;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private UserRole userRole;
}
