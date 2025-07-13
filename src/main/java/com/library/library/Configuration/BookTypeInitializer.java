package com.library.library.Configuration;

import com.library.library.Model.Enumerations.Category;
import com.library.library.Model.BookType;
import com.library.library.Repository.BookTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Slf4j
@Component
@Transactional
public class BookTypeInitializer implements CommandLineRunner {

    @Autowired
    private  BookTypeRepository bookTypeRepository;

    @Override
    public void run(String... args) throws Exception {
        if (bookTypeRepository.count() == 0) {
            createInitialTypes();
        }
    }
    private void createInitialTypes() {
        for (Category category : Category.values()) {
            BookType bookType = new BookType();
            bookType.setName(category.getDisplayName());
            bookType.setSlug(category.getSlug());
            bookTypeRepository.save(bookType);
            log.debug("Created book type: {}", category.getDisplayName());
        }
    }
}