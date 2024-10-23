package com.eLearning.services.auth;

import com.eLearning.dto.SignupRequest;
import com.eLearning.dto.UserDto;
import com.eLearning.entity.User;
import com.eLearning.enums.UserRole;
import com.eLearning.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    
    @PostConstruct
    public void createAdminAccount(){
        User adminAccount = userRepository.findByUserRole(UserRole.Admin);
        if(adminAccount == null){
            User newAdminAccount = new User();
            newAdminAccount.setUsername("admin");
            newAdminAccount.setEmail("admin@test.com");
            newAdminAccount.setPassword(new BCryptPasswordEncoder().encode("admin"));
            newAdminAccount.setUserRole(UserRole.Admin);
            userRepository.save(newAdminAccount);
            System.out.println("Admin account created");
        }
    }

    @Override
    public UserDto createUser(SignupRequest signupRequest) {
        User user = new User();
        user.setUsername(signupRequest.getName());
        user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));
        user.setEmail(signupRequest.getEmail());
        user.setUserRole(UserRole.User);
        User createdUser = userRepository.save(user);
        UserDto userDto = new UserDto();
        userDto.setId(createdUser.getId());

        return userDto;
    }

    @Override
    public boolean hasUserWithEmail(String email) {
        return userRepository.findFirstByEmail(email).isPresent();
    }
}
