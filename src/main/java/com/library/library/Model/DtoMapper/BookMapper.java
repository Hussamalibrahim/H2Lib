package com.library.library.Model.DtoMapper;

import com.library.library.Model.Dto.BookDto;
import com.library.library.Model.Book;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BookMapper {

    public static Book convertToEntity(BookDto bookDto) {
        Book book = new Book();

        book.setId(bookDto.getId());
        book.setBookKey(bookDto.getBookKey());
        book.setBookTitle(bookDto.getBookTitle());
        book.setIsbn(bookDto.getIsbn());

        book.setPublisherKey(bookDto.getPublisherKey());
        book.setPublisherName(bookDto.getPublisherName());

        book.setAuthorName(bookDto.getAuthorName());
        book.setAuthorKey(bookDto.getAuthorKey());

        book.setPublicationYear(book.getPublicationYear());

        book.setWriteYear(book.getWriteYear());


        book.setRatingAvg(bookDto.getRatingAvg());
        book.setRatingNumber(bookDto.getRatingNumber());
        book.setDownloadingNumber(bookDto.getDownloadingNumber());

        book.setImageGoogleDriveId(bookDto.getImageGoogleDriveId());
        book.setImageCoverDirectLink(bookDto.getImageCoverDirectLink());

        book.setBookFileId(bookDto.getBookFileId());
        book.setBookSize(bookDto.getBookSize());
        book.setFileContentType(bookDto.getFileContentType());

        return book;
    }
    public static BookDto convertToDto(Book book) {
        BookDto bookDto = new BookDto();


        bookDto.setId(book.getId());
        bookDto.setBookKey(book.getBookKey());
        bookDto.setBookTitle(book.getBookTitle());
        bookDto.setIsbn(book.getIsbn());

        bookDto.setPublisherKey(book.getPublisherKey());
        bookDto.setPublisherName(book.getPublisherName());

        bookDto.setAuthorName(book.getAuthorName());
        bookDto.setAuthorKey(book.getAuthorKey());

        bookDto.setPublicationYear(book.getPublicationYear());

        bookDto.setWriteYear(book.getWriteYear());


        bookDto.setRatingAvg(book.getRatingAvg());
        bookDto.setRatingNumber(book.getRatingNumber());
        bookDto.setDownloadingNumber(book.getDownloadingNumber());

        bookDto.setImageGoogleDriveId(book.getImageGoogleDriveId());
        bookDto.setImageCoverDirectLink(book.getImageCoverDirectLink());

        bookDto.setBookFileId(book.getBookFileId());
        bookDto.setBookSize(book.getBookSize());
        bookDto.setFileContentType(book.getFileContentType());


        return bookDto;
    }
}
