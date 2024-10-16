package com.example.Ecommercial_project.Shoping.website.controller;

import com.example.Ecommercial_project.Shoping.website.model.Category;
import com.example.Ecommercial_project.Shoping.website.model.Product;
import com.example.Ecommercial_project.Shoping.website.service.CategoryService;
import com.example.Ecommercial_project.Shoping.website.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private CategoryService categoryService;
    private ProductService productService;

    @Autowired
    public HomeController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @GetMapping("/product")
    public String showProducts(Model model, @RequestParam(value = "category", defaultValue = "") String category){

       List<Category> categories = categoryService.getAllActiveCategory();
       List<Product> products = productService.getAllActiveProduct(category);

       model.addAttribute("categories",categories);
       model.addAttribute("products",products);
       //truyền param sang để thực hiện bôi đen danh mục được chọn
       model.addAttribute("paramValue",category);


        return "product";
    }

    @GetMapping("/view_product/{id}")
    public String viewDetailProduct(@PathVariable int id, Model model){
        Product productById = productService.findProductById(id);
        model.addAttribute("product",productById);
        return "view_product";
    }
}
