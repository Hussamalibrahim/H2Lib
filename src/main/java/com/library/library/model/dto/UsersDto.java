package com.library.library.model.dto;

import com.library.library.model.enumerations.AuthorStatus;
import com.library.library.model.enumerations.Country;
import com.library.library.model.enumerations.Gender;
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
