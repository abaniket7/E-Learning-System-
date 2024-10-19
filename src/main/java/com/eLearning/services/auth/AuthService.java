package com.eLearning.services.auth;

import com.eLearning.dto.SignupRequest;
import com.eLearning.dto.UserDto;

public interface AuthService {
   UserDto createUser(SignupRequest signupRequest);

   boolean hasUserWithEmail(String email);
}



