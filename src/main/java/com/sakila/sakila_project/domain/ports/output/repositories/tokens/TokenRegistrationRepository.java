package com.sakila.sakila_project.domain.ports.output.repositories.tokens;

import com.sakila.sakila_project.domain.model.tokens.TokenRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRegistrationRepository extends JpaRepository<TokenRegistration, Integer>{
    Optional<TokenRegistration> findTokenRegistrationByTokenAndRefreshToken(String token, String refreshToken);
}
