package com.finance.FinanceManagement.config;

import com.finance.FinanceManagement.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( "/register","/login","/users","/verify-otp","/2fa","/2fa/qr","/dashboard","/images","/home",
                                "/forgot-password", "/forgot-password-otp", "/verify-forgot-password-otp", "/forgot-password-authenticator",
                                "/verify-forgot-password-authenticator", "/reset-password").permitAll()
                        .anyRequest().authenticated()
                )
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .permitAll())
                        .addFilterBefore(
                                jwtAuthenticationFilter,
                                UsernamePasswordAuthenticationFilter.class
                        );
        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService(
//            PasswordEncoder passwordEncoder) {
//
//        UserDetails admin = User.builder()
//                .username("admin@gmail.com")
//                .password(passwordEncoder.encode("admin123"))
//                .roles("ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin);
//    }



}