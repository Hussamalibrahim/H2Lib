package com.library.library.service.Implements;

import com.library.library.repository.RatingRepository;
import com.library.library.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RatingServiceImp implements RatingService {
    @Autowired
    private RatingRepository ratingRepository;




}
