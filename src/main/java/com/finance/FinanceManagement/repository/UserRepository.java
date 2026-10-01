package com.finance.FinanceManagement.repository;

import com.finance.FinanceManagement.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<Users,Integer> {
    Optional<Users> findByEmail(String email);
}
