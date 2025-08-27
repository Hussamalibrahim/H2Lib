package com.library.library.Service.Implements;

import com.library.library.Model.Dto.LibraryDto;
import com.library.library.Model.DtoMapper.LibraryMapper;
import com.library.library.Model.Library;
import com.library.library.Repository.LibraryRepository;
import com.library.library.Service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibraryServiceImp implements LibraryService {

    @Autowired
    private LibraryRepository libraryRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "libraryCache", unless = "#result == null")
    public LibraryDto getStats() {
        return LibraryMapper.convertToDto(libraryRepository.findLibraryStats());
    }

    @Transactional
    @CacheEvict(value = "libraryCache", allEntries = true)
    public void incrementUsers() {
        libraryRepository.incrementUsers();
    }

    @Transactional
    @CacheEvict(value = "libraryCache", allEntries = true)
    public void incrementBooks() {
        libraryRepository.incrementBooks();
    }

    @Transactional
    @CacheEvict(value = "libraryCache", allEntries = true)
    public void incrementDownloads() {
        libraryRepository.incrementDownloads();
    }

    @Transactional
    @CacheEvict(value = "libraryCache", allEntries = true)
    public void incrementAuthors() {
        libraryRepository.incrementAuthors();
    }

    @Transactional
    @CacheEvict(value = "libraryCache", allEntries = true)
    public void incrementRating() {
        libraryRepository.incrementRating();
    }

    @Transactional
    @CacheEvict(value = "libraryCache", allEntries = true)
    public void updateStats(Library stats) {
        Library existing = libraryRepository.findLibraryStats();
        if (existing != null) {
            existing.setTotalUsers(stats.getTotalUsers());
            existing.setTotalBooks(stats.getTotalBooks());
            existing.setTotalDownloads(stats.getTotalDownloads());
            existing.setTotalAuthors(stats.getTotalAuthors());
            existing.setTotalRating(stats.getTotalRating());
            libraryRepository.save(existing);
        }
    }

    public long countById() {
        return libraryRepository.countById();
    }
    public Library save(Library library){
        return libraryRepository.save(library);
    }
}