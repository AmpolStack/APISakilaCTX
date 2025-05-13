package com.sakila.sakila_project.domain.ports.output.repositories.sakila;

import com.sakila.sakila_project.domain.model.sakila.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer>{

    @Query("SELECT s FROM Staff s JOIN FETCH s.address WHERE s.username = :username AND s.password = :password")
    Optional<Staff> findByUsernameAndPasswordWithAddress(String username, String password);

    @Query("SELECT s FROM Staff s JOIN FETCH s.store st JOIN FETCH s.address JOIN FETCH st.address WHERE s.id = :id")
    Optional<Staff> findByIdWithStoreAndAddress(int id);

    @Query("SELECT s FROM Staff s JOIN FETCH s.address WHERE s.id = :id")
    Optional<Staff> findByIdWithAddress(int id);
}
