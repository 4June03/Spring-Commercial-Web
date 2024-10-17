package com.example.Ecommercial_project.Shoping.website.service.ServiceImpl;

import com.example.Ecommercial_project.Shoping.website.model.UserDtls;
import com.example.Ecommercial_project.Shoping.website.repository.UserRepository;
import com.example.Ecommercial_project.Shoping.website.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDtls saveUser(UserDtls user) {
        UserDtls savedUser = userRepository.save(user);
        return savedUser;
    }
}
