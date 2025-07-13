package com.library.library.Service;

import com.library.library.Model.Dto.UsersDto;
import com.library.library.Model.Users;

public interface UserService {
    void save(UsersDto adminUser);

    Users save(Users adminUser);

    boolean existsByDisplayName(String name);
}
