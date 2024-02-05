package com.vieira.joao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vieira.joao.model.AppUser;

import java.util.Optional;

public interface UserRepo extends JpaRepository<AppUser, Integer> {
    void deleteUserById(Integer id);

    Optional<AppUser> findUserById(Integer id);

    Optional<AppUser> findUserByUsername(String username);
}
