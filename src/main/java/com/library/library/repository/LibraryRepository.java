package com.library.library.repository;

import com.library.library.model.Library;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Short> {

    @Query(value = "SELECT * FROM library l WHERE l.library_id = 1",nativeQuery = true)
    Library findLibraryStats();


    @Modifying
    @Transactional
    @Query(value = "UPDATE library l SET l.total_users = l.total_users + 1 WHERE l.library_id = 1",nativeQuery = true)
    void incrementUsers();

    @Modifying
    @Transactional
    @Query(value = "UPDATE library l SET l.total_books = l.total_books + 1 WHERE l.library_id = 1",nativeQuery = true)
    void incrementBooks();

    @Modifying
    @Transactional
    @Query(value = "UPDATE library l SET l.total_downloads = l.total_downloads + 1 WHERE l.library_id = 1" ,nativeQuery = true)
    void incrementDownloads();

    @Modifying
    @Transactional
    @Query(value = "UPDATE library l SET l.total_authors = l.total_authors + 1 WHERE l.library_id = 1" , nativeQuery = true)
    void incrementAuthors();

    @Modifying
    @Transactional
    @Query(value = "UPDATE library l SET l.total_rating = l.total_rating +1 WHERE l.library_id = 1",nativeQuery = true)
     void incrementRating();

    @Query(value = "SELECT COUNT(*) library",nativeQuery = true)
    long countById();
}
