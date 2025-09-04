package com.library.library.model.dtoMapper;

import com.library.library.model.dto.BookListDto;
import com.library.library.model.BookList;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BookListMapper {

    public static BookList convertToEntity(BookListDto bookListDto) {
        BookList bookList = new BookList();

        bookList.setId(bookListDto.getId());
        bookList.setName(bookListDto.getName());
        bookList.setCreatedDate(bookListDto.getCreatedDate());
        bookList.setIsDefault(bookListDto.getIsDefault());

        return bookList;
    }

    public static BookListDto convertToDto(BookList bookList) {
        BookListDto bookListDto = new BookListDto();

        bookListDto.setId(bookList.getId());
        bookListDto.setName(bookList.getName());
        bookListDto.setCreatedDate(bookList.getCreatedDate());
        bookListDto.setIsDefault(bookList.getIsDefault());

        return bookListDto;

    }
}
