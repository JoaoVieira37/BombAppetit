package sirs.a14.bombappetit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sirs.a14.bombappetit.model.AppUser;

import java.util.Optional;

public interface UserRepo extends JpaRepository<AppUser, Integer> {
    void deleteUserById(Integer id);

    Optional<AppUser> findUserById(Integer id);

    Optional<AppUser> findUserByUsername(String username);
}
