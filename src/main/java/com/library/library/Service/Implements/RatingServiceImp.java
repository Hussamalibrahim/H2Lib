package com.library.library.Service.Implements;

import com.library.library.Repository.RatingRepository;
import com.library.library.Service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RatingServiceImp implements RatingService {
    @Autowired
    private RatingRepository ratingRepository;




}
