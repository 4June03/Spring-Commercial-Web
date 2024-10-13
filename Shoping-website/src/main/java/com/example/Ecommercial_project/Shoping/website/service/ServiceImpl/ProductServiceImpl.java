package com.example.Ecommercial_project.Shoping.website.service.ServiceImpl;

import com.example.Ecommercial_project.Shoping.website.model.Product;
import com.example.Ecommercial_project.Shoping.website.repository.ProductRepository;
import com.example.Ecommercial_project.Shoping.website.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }
}
