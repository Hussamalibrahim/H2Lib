package com.library.library.Service;

import com.library.library.Model.Dto.BookDto;

import java.util.List;

public interface BookService {
    void incrementRatingNumber(long id);
    void updateRating(double rating, long bookId);
    void incrementDownloadNumber(long id);
    void save(BookDto bookDto);
    List<BookDto> findAll();
}
