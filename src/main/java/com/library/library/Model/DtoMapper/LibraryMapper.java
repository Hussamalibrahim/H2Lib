package com.library.library.Model.DtoMapper;

import com.library.library.Model.Dto.LibraryDto;
import com.library.library.Model.Library;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LibraryMapper {


    public static Library convertToEntity(LibraryDto libraryDto) {
        Library library = new Library();

        library.setId(libraryDto.getId());
        library.setTotalUsers(libraryDto.getTotalUsers());
        library.setTotalBooks(libraryDto.getTotalBooks());
        library.setTotalDownloads(libraryDto.getTotalDownloads());
        library.setTotalAuthors(libraryDto.getTotalAuthors());
        library.setTotalRating(libraryDto.getTotalRating());

        return library;
    }

    public static LibraryDto convertToDto(Library library) {
        LibraryDto libraryDto = new LibraryDto();

        libraryDto.setId(library.getId());
        libraryDto.setTotalUsers(library.getTotalUsers());
        libraryDto.setTotalBooks(library.getTotalBooks());
        libraryDto.setTotalDownloads(library.getTotalDownloads());
        libraryDto.setTotalRating(library.getTotalRating());

        return libraryDto;
    }

}
