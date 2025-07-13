package com.library.library.Service.Implements;

import com.library.library.Model.Dto.UsersDto;
import com.library.library.Model.DtoMapper.UserMapper;
import com.library.library.Model.Users;
import com.library.library.Repository.UserRepository;
import com.library.library.Service.UserService;
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
