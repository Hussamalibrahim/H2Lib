package com.library.library.Model.Dto;

import com.library.library.Model.Enumerations.AuthorStatus;
import com.library.library.Model.Enumerations.Country;
import com.library.library.Model.Enumerations.Gender;
import com.library.library.Model.Enumerations.Role;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class UsersDto {
    private Long id;
    private String displayName;
    private String usernameKey;
    private AuthorStatus authorStatus;
    private String bio;
    private Date dateOfJoin;
    private Integer notificationNumber;
    private Gender gender;
    private Map<LocalDate, Boolean> dateOfBirth;
    private String imageUrl;
    private String imageContentType;
    private Map<Country, Boolean> country;
//    private Map<String, Boolean> carer;



}
