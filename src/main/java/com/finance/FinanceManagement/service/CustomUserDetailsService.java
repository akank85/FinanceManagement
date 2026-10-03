package com.finance.FinanceManagement.service;
import com.finance.FinanceManagement.entity.Users;
import com.finance.FinanceManagement.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
@Service
public class CustomUserDetailsService implements UserDetailsService {

private  final UserRepository userRepository;
public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
}

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      Users users = userRepository.findByEmail(username)
              .orElseThrow();
      return User.withUsername(users.getEmail())
              .password(users.getPasswordHash())
              .roles(users.getRole())
              .build();

    }
}
