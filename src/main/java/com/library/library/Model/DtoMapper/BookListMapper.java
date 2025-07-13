package com.library.library.Model.DtoMapper;

import com.library.library.Model.Dto.BookListDto;
import com.library.library.Model.BookList;
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
