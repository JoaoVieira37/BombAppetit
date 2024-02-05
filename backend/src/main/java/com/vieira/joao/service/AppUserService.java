package com.vieira.joao.service;

import com.vieira.joao.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vieira.joao.model.AppUser;

import java.util.List;

@Service
@Transactional
public class AppUserService {
    private final UserRepo userRepo;

    @Autowired
    public AppUserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public AppUser addUser(AppUser appUser) {
        return userRepo.save(appUser);
    }

    public AppUser updateUser(AppUser appUser) {
        return userRepo.save(appUser);
    }

    public List<AppUser> findAllUsers() {
        return userRepo.findAll();
    }

    public AppUser findUserByUsername(String username) {
        return userRepo.findUserByUsername(username).orElseThrow();
    }

    public AppUser findUserById(Integer id) {
        return userRepo.findUserById(id).orElseThrow();
    }

    public void deleteUserById(Integer id) {
        userRepo.deleteUserById(id);
    }
}

