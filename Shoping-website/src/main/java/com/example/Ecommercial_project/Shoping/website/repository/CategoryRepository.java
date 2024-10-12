package com.example.Ecommercial_project.Shoping.website.repository;

import com.example.Ecommercial_project.Shoping.website.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Integer> {
    //Định nghĩa để JPA cung cấp phương thức tìm Cate by Name
    public Boolean existsByName(String name);
}
