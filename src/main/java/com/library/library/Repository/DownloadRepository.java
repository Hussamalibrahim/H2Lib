package com.library.library.Repository;

import com.library.library.Model.Book;
import com.library.library.Model.Downloads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DownloadRepository extends JpaRepository<Downloads,Long> {

    @Query(value = "SELECT * FROM downloads WHERE book_id = :bookId", nativeQuery = true)
    List<Downloads> findByBookId(@Param("bookId") Long bookId);

    @Query(value = "SELECT b.* FROM book b INNER JOIN downloads d ON b.book_id = d.book_id WHERE d.user_id = :userId", nativeQuery = true)
    List<Book> findBooksByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT b.* FROM book bWHERE b.book_id IN (SELECT d.book_id FROM downloads d WHERE d.user_id = :userId )", nativeQuery = true)
    List<Book> findDownloadedBooksByUserNative(@Param("userId") Long userId);
}
