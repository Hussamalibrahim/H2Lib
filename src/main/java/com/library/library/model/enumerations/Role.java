package com.library.library.model.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum Role {
    AUTHOR, USER, ADMIN, MODERATOR,EXPIRED
}
