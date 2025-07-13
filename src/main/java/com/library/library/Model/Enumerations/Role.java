package com.library.library.Model.Enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum Role {
    AUTHOR, USER, ADMIN, MODERATOR,EXPIRED
}
