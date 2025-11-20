package com.library.library.model.dtoMapper;

import com.library.library.model.dto.BookTypeDto;
import com.library.library.model.BookType;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BookTypeMapper {

    public static BookTypeDto convertToDto(BookType bookType){
        BookTypeDto  bookTypeDto = new BookTypeDto();

        bookTypeDto.setId(bookType.getId());
        bookTypeDto.setName(bookType.getName());
        bookTypeDto.setSlug(bookType.getName());
        bookTypeDto.setBookCount(bookType.getBookCount());

        return bookTypeDto;
    }

    public static BookType convertToEntity(BookTypeDto bookTypeDto){
        BookType bookType = new BookType();

        bookType.setId(bookTypeDto.getId());
        bookType.setName(bookTypeDto.getName());
        bookType.setSlug(bookTypeDto.getName());
        bookType.setBookCount(bookTypeDto.getBookCount());

        return bookType;
    }


}
