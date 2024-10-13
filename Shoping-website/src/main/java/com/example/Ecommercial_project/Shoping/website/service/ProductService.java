package com.example.Ecommercial_project.Shoping.website.service;

import com.example.Ecommercial_project.Shoping.website.model.Product;

import java.util.List;

public interface ProductService {
    public Product saveProduct(Product product);

    public List<Product> getAllProduct();
}
