package com.library.library.repository;

import com.library.library.model.BookType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookTypeRepository extends JpaRepository<BookType,Long> {

    @NotNull
    @Override
    @Query(value = "SELECT * FROM book_types where (book_count > 0)" , nativeQuery = true)
    List<BookType> findAll();

    boolean existsByName(String name);
    BookType findBySlug(String slug);
    BookType findByName(String name);
}
