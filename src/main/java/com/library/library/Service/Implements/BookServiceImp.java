package com.library.library.Service.Implements;

import com.library.library.Model.Dto.BookDto;
import com.library.library.Model.DtoMapper.BookMapper;
import com.library.library.Exception.infrastructure.BookAlreadyExistsException;
import com.library.library.Model.Book;
import com.library.library.Repository.BookRepository;
import com.library.library.Service.BookService;
import com.library.library.Service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class BookServiceImp implements BookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private LibraryService libraryService;

    @Transactional
    @CachePut(value = "bookCache", key = "book.id")
    public void incrementRatingNumber(long id) {
        bookRepository.incrementRatingNumber(id);
    }

    @Transactional
    @CachePut(value = "bookCache", key = "book.id")
    public void incrementDownloadNumber(long id) {
        bookRepository.incrementDownloadNumber(id);
    }

    @Transactional
    @CachePut(value = "bookCache")
    @Override
    public void save(BookDto bookDto) {
//        this.checkViolation(bookDto);
        bookRepository.save(BookMapper.convertToEntity(bookDto));
    }

    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(BookMapper::convertToDto)
                .toList();
    }

    //FIXME
    @Transactional
    @CachePut(value = "bookCache", key = "book.id")
    public void updateRating(double newRating, long id) {
        bookRepository.incrementRatingNumber(id);

        double currentAvg = bookRepository.findRatingById(id).orElse(0.0);

        long ratingCount = bookRepository.findById(id)
                .map(Book::getRatingNumber)
                .orElse(1L);

        double newAvg = currentAvg + ((newRating - currentAvg) / ratingCount);

        bookRepository.updateRatingAvg(id, newAvg);
    }

    // Helper Method
//    private void checkViolation(BookDto bookDto) {
//        if (bookRepository.existsByBookTitle(bookDto.getBookTitle()))
//            throw new BookAlreadyExistsException("Book with Title '" + bookDto.getBookTitle() + "' is already exists");
//        if (bookRepository.existsByIsbn(bookDto.getIsbn()))
//            throw new BookAlreadyExistsException("Book with Isbn '" + bookDto.getIsbn() + "' is already exists");
//
//    }
}