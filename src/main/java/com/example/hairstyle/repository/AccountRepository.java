package com.example.hairstyle.repository;

import com.example.hairstyle.entity.Account;
import com.example.hairstyle.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    Optional<Account> findByUsername(String username);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByVerificationCode(String verificationCode);

    Boolean existsAccountByUsername(String username);

    Boolean existsAccountByEmail(String email);

    @Override
    Page<Account> findAll(Pageable pageable);

    Page<Account> findByUsernameContaining(String username, Pageable pageable);

}
