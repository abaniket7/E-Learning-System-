package com.eLearning.controller;

import com.eLearning.dto.AuthenticationRequest;
import com.eLearning.dto.AuthenticationResponse;
import com.eLearning.dto.SignupRequest;
import com.eLearning.dto.UserDto;
import com.eLearning.entity.User;
import com.eLearning.repository.UserRepository;
import com.eLearning.services.auth.AuthService;
import com.eLearning.services.jwt.UserService;
import com.eLearning.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@RequestBody SignupRequest signupRequest) {
        if (authService.hasUserWithEmail(signupRequest.getEmail())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User already exists with the same email.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT); // Returning 409 Conflict
        }

        UserDto createdUserDto = authService.createUser(signupRequest);
        if (createdUserDto == null) {
            return new ResponseEntity<>("User not created, come again later.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {

        // Step 1: Check if the user exists based on the provided email
        Optional<User> optionalUser = userRepository.findFirstByEmail(authenticationRequest.getEmail());

        if (!optionalUser.isPresent()) {
            // If the user does not exist, return a 404 Not Found response with the message "User not found."
            Map<String, String> response = new HashMap<>();
            response.put("message", "User not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Step 2: If the user exists, try to authenticate with the password
        try {
            // Attempt authentication (this checks if the password is correct)
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            // If authentication fails due to bad credentials (wrong password), return 401 Unauthorized
            Map<String, String> response = new HashMap<>();
            response.put("message", "Incorrect email or password.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Step 3: Authentication successful, proceed to generate JWT and return user details
        final UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setJwt(jwt);
        authenticationResponse.setUserId(optionalUser.get().getId());
        authenticationResponse.setUserRole(optionalUser.get().getUserRole());

        // Return the successful response with the JWT token and user details
        return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
    }
}
