package com.example.Ecommercial_project.Shoping.website.repository;

import com.example.Ecommercial_project.Shoping.website.model.UserDtls;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDtls, Integer> {
}
