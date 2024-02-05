package com.vieira.joao.service;

import com.vieira.joao.repository.MenuEntryRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vieira.joao.model.MenuEntry;

import java.util.List;

@Service
@Transactional
public class MenuEntryService {
    private final MenuEntryRepo menuEntryRepo;

    @Autowired
    public MenuEntryService(MenuEntryRepo menuEntryRepo) {
        this.menuEntryRepo = menuEntryRepo;
    }

    public MenuEntry addMenuEntry(MenuEntry menuEntry) {
        return menuEntryRepo.save(menuEntry);
    }

    public List<MenuEntry> findAllMenuEntries() {
        return menuEntryRepo.findAll();
    }

    public MenuEntry updateMenuEntry(MenuEntry menuEntry) {
        return menuEntryRepo.save(menuEntry);
    }

    public MenuEntry findMenuEntry(Integer id) {
        return menuEntryRepo.findMenuEntryById(id).orElseThrow();
    }

    public void deleteMenuEntry(Integer id) {
        menuEntryRepo.deleteMenuEntryById(id);
    }
}
