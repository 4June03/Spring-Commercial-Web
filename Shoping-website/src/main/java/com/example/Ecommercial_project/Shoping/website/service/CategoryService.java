package com.example.Ecommercial_project.Shoping.website.service;

import com.example.Ecommercial_project.Shoping.website.model.Category;

import java.util.List;

public interface CategoryService {

    public Boolean existCategory(String name);

    public Category saveCategory(Category category);

    public List<Category> getAllCategory();
}
