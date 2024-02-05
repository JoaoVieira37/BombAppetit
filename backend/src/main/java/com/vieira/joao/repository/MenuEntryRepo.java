package com.vieira.joao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vieira.joao.model.MenuEntry;

import java.util.Optional;

public interface MenuEntryRepo extends JpaRepository<MenuEntry, Integer> {
    void deleteMenuEntryById(Integer id);

    Optional<MenuEntry> findMenuEntryById(Integer id);
}
