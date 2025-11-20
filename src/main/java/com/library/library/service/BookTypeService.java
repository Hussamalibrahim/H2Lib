package com.library.library.service;


import com.library.library.model.dto.BookTypeDto;

import java.util.List;

public interface BookTypeService {
    List<BookTypeDto> findAll();
}
