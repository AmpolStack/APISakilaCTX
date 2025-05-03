package com.sakila.sakila_project.application.adapters.output.repositories.sakila;

import com.sakila.sakila_project.domain.model.sakila.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer>{
    Optional<Staff> findStaffByUsernameAndPassword(String username, String password);
}
