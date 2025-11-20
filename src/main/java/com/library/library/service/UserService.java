package com.library.library.service;

import com.library.library.model.dto.UsersDto;
import com.library.library.model.Users;

public interface UserService {
    void save(UsersDto adminUser);

    Users save(Users adminUser);

    boolean existsByDisplayName(String name);
}
