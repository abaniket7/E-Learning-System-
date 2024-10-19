package com.eLearning.controller;

import com.eLearning.dto.SignupRequest;
import com.eLearning.dto.UserDto;
import com.eLearning.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup") // Added PostMapping for the signup endpoint
    public ResponseEntity<?> signupUser(@RequestBody SignupRequest signupRequest) {

        if (authService.hasUserWithEmail(signupRequest.getEmail())) {
            return new ResponseEntity<>("User already exists with the same email.",HttpStatus.NOT_ACCEPTABLE);
        }
        UserDto createdUserDto = authService.createUser(signupRequest);

        if (createdUserDto == null) {
            return new ResponseEntity<>("User not created, come again later.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);
    }
}
