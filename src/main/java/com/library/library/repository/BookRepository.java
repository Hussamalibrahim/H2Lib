package com.library.library.repository;

import com.library.library.model.enumerations.PublishStatus;
import com.library.library.model.Book;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface BookRepository extends JpaRepository<Book, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE Book b SET b.ratingNumber = b.ratingNumber + 1 WHERE b.id = :id",nativeQuery = true)
    void incrementRatingNumber(@Param("id") long id);

    @Query(value = "SELECT b.ratingAvg FROM Book b WHERE b.id = :id",nativeQuery = true)
    Optional<Double> findRatingById(@Param("id") long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Book b SET b.ratingAvg = :newAvg WHERE b.id = :id",nativeQuery = true)
    void updateRatingAvg(@Param("id") long id, @Param("newAvg") double newAvg);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Book b SET b.downloading_number = b.downloading_number + 1 WHERE b.id = :id",nativeQuery = true)
    void incrementDownloadNumber(long id);

    List<Book> findByIsListedTrue();

    Optional<Book> findByStatus(PublishStatus status);

    boolean existsByBookTitle(String bookTitle);

    boolean existsByIsbn(String isbn);
}
