package com.example.Ecommercial_project.Shoping.website.service;

import com.example.Ecommercial_project.Shoping.website.model.UserDtls;

import java.util.List;

public interface UserService {

    public UserDtls saveUser(UserDtls user);

    public UserDtls getUserByEmail(String email);

    public List<UserDtls> getAllUsers(String role);

    Boolean updateAccountStatus(Integer id, Boolean status);
}
