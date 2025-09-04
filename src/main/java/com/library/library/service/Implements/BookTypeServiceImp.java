package com.library.library.service.Implements;

import com.library.library.model.dto.BookTypeDto;
import com.library.library.model.dtoMapper.BookTypeMapper;
import com.library.library.repository.BookTypeRepository;
import com.library.library.service.BookTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class BookTypeServiceImp implements BookTypeService {

    @Autowired
    BookTypeRepository bookTypeRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "bookTypeCache", key = "'allBookTypes'")
    public List<BookTypeDto> findAll() {
        return bookTypeRepository.findAll().stream()
                .map(BookTypeMapper::convertToDto)
                .toList();
    }
}
