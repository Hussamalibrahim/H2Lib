package com.library.library.service;

import com.library.library.model.dto.LibraryDto;
import com.library.library.model.Library;

public interface LibraryService {

    LibraryDto getStats();

    void incrementUsers();

    void incrementBooks();

    void incrementDownloads();

    void incrementAuthors();

    void incrementRating();

    void updateStats(Library stats);

    long countById();

    Library save(Library library);
}
