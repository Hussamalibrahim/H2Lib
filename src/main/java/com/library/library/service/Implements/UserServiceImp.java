package com.library.library.service.Implements;

import com.library.library.model.dto.UsersDto;
import com.library.library.model.dtoMapper.UserMapper;
import com.library.library.model.Users;
import com.library.library.repository.UserRepository;
import com.library.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Users save(Users user) {
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    public void save(UsersDto user) {
        userRepository.save(UserMapper.convertToEntity(user));
    }

    public boolean existsByDisplayName(String name){
        return userRepository.existsByDisplayName(name);
    }


}
