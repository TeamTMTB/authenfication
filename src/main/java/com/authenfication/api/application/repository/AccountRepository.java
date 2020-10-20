package com.authenfication.api.application.repository;

import com.authenfication.api.application.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountId(String email);

    Optional<Account> findByAccountIdAndProvider(String accountId, String provider);
}
