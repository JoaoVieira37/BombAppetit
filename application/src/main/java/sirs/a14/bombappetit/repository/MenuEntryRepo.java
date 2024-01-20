package sirs.a14.bombappetit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sirs.a14.bombappetit.model.MenuEntry;

import java.util.Optional;

public interface MenuEntryRepo extends JpaRepository<MenuEntry, Integer> {
    void deleteMenuEntryById(Integer id);

    Optional<MenuEntry> findMenuEntryById(Integer id);
}
