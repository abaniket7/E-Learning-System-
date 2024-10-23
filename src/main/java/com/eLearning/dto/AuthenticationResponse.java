package com.eLearning.dto;

import com.eLearning.enums.UserRole;
import lombok.Data;

@Data
public class AuthenticationResponse {
    private String jwt;
    private UserRole userRole;
    private Long userId;
}
