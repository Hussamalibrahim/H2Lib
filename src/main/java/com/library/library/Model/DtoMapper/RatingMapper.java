package com.library.library.Model.DtoMapper;

import com.library.library.Model.Dto.RatingDto;
import com.library.library.Model.Rating;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RatingMapper {


    public static Rating dtoToEntity(RatingDto ratingDto) {
        Rating rating = new Rating();
        rating.setId(ratingDto.getId());
        rating.setRating(ratingDto.getRating());
        rating.setDateOfRating(ratingDto.getDateOfRating());
        return rating;
    }

    public static RatingDto EntityToDto(Rating rating) {
        RatingDto ratingDto = new RatingDto();

        ratingDto.setId(rating.getId());
        ratingDto.setRating(rating.getRating());
        ratingDto.setDateOfRating(rating.getDateOfRating());

        return ratingDto;
    }
}
