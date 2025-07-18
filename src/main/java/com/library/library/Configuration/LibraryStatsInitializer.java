package com.library.library.Configuration;

import com.library.library.Model.Library;
import com.library.library.Repository.LibraryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class LibraryStatsInitializer implements CommandLineRunner {

    private final LibraryRepository libraryStatsRepository;

    @Override
    public void run(String... args) {
        if (libraryStatsRepository.count() == 0) {
            Library stats = new Library();
            libraryStatsRepository.save(stats);
            log.info("Initialized default library stats");
        }
    }
}