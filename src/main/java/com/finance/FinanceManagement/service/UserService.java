package com.finance.FinanceManagement.service;

import com.finance.FinanceManagement.dto.LoginRequest;
import com.finance.FinanceManagement.dto.UserRequest;
import com.finance.FinanceManagement.entity.Users;
import com.finance.FinanceManagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private  final UserRepository userRepository;
    private  final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
  public Users createUser(UserRequest userRequest) {
        Users users = new Users();
        users.setFullName(userRequest.getFullName());
        users.setEmail(userRequest.getEmail());
        users.setPasswordHash(passwordEncoder.encode(userRequest.getPasswordHash()));
        users.setRole(userRequest.getRole());
        users.setIsFirstTimeLogin(true);
        return userRepository.save(users);
    }

    public Users login (LoginRequest loginRequest) {
       Users users = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() ->
                       new RuntimeException("User not found"));
        ;
       if (!passwordEncoder.matches(loginRequest.getPassword(), users.getPasswordHash())) {
           throw new RuntimeException("Wrong password");
       }
       return users;

    }

}
