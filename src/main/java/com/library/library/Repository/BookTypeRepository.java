package com.library.library.Repository;

import com.library.library.Model.BookType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
