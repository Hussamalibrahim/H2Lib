package com.library.library.Service;


import com.library.library.Model.Dto.BookTypeDto;

import java.util.List;

public interface BookTypeService {
    List<BookTypeDto> findAll();
}
