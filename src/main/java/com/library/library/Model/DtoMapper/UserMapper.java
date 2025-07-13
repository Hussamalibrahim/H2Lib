package com.library.library.Model.DtoMapper;

import com.library.library.Model.Dto.UsersDto;
import com.library.library.Model.Users;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserMapper {

    public static Users convertToEntity(UsersDto userDto) {
        Users user = new Users();

        user.setId(userDto.getId());
        user.setDisplayName(userDto.getDisplayName());
        user.setUsernameKey(userDto.getUsernameKey());
        user.setAuthorStatus(userDto.getAuthorStatus());
        user.setBio(userDto.getBio());
        user.setDateOfJoin(userDto.getDateOfJoin());

        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setCountry(userDto.getCountry());
//        user.setCarer(userDto.getCarer());
        user.setGender(userDto.getGender());

        user.setImageContentType(userDto.getImageContentType());
        user.setImageUrl(userDto.getImageUrl());



        return user;
    }
    public static UsersDto convertToDto(Users user) {
        UsersDto userDto = new UsersDto();

        userDto.setId(user.getId());
        userDto.setDisplayName(user.getDisplayName());
        userDto.setUsernameKey(user.getUsernameKey());
        userDto.setAuthorStatus(user.getAuthorStatus());
        userDto.setBio(user.getBio());
        userDto.setDateOfJoin(user.getDateOfJoin());

        userDto.setDateOfBirth(user.getDateOfBirth());
        userDto.setCountry(user.getCountry());
//        userDto.setCarer(user.getCarer());
        userDto.setGender(user.getGender());

        userDto.setImageContentType(user.getImageContentType());
        userDto.setImageUrl(user.getImageUrl());

        return userDto;
    }
}
