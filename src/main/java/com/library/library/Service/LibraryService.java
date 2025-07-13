package com.library.library.Service;

import com.library.library.Model.Dto.LibraryDto;
import com.library.library.Model.Library;

public interface LibraryService {

    LibraryDto getStats();
    void incrementUsers();
    void incrementBooks();
    void incrementDownloads();
    void incrementAuthors();
    void incrementRating();
    void updateStats(Library stats);
}
