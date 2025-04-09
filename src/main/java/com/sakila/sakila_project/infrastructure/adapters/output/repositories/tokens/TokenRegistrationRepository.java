package com.sakila.sakila_project.infrastructure.adapters.output.repositories.tokens;

import com.sakila.sakila_project.domain.model.tokens.TokenRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRegistrationRepository extends JpaRepository<TokenRegistration, Integer>{
}
