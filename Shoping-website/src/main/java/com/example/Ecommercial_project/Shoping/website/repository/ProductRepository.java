package com.example.Ecommercial_project.Shoping.website.repository;

import com.example.Ecommercial_project.Shoping.website.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
