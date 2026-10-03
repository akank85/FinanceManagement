package com.finance.FinanceManagement.repository;

import com.finance.FinanceManagement.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<Users,Integer> {
    Optional<Users> findByEmail(String email);
}
