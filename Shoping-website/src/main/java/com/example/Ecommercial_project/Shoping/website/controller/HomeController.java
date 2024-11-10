package com.example.Ecommercial_project.Shoping.website.controller;

import com.example.Ecommercial_project.Shoping.website.model.Cart;
import com.example.Ecommercial_project.Shoping.website.model.Category;
import com.example.Ecommercial_project.Shoping.website.model.Product;
import com.example.Ecommercial_project.Shoping.website.model.UserDtls;
import com.example.Ecommercial_project.Shoping.website.service.CartService;
import com.example.Ecommercial_project.Shoping.website.service.CategoryService;
import com.example.Ecommercial_project.Shoping.website.service.ProductService;
import com.example.Ecommercial_project.Shoping.website.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    private CategoryService categoryService;
    private ProductService productService;
    private UserService userService;
    private CartService cartService;

    @Autowired
    public HomeController(CategoryService categoryService, ProductService productService, UserService userService, CartService cartService) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.userService = userService;
        this.cartService = cartService;
    }

    @ModelAttribute
    public void getUserDetails(Principal p, Model model){
        if(p!=null){
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            model.addAttribute("user",userDtls);
            Integer countCart =  cartService.getCountCart(userDtls.getId());
            //số lượng sản phẩm đang có trong giỏ
            model.addAttribute("countCart",countCart);
        }else {
            model.addAttribute("user",null);
        }

        //Lấy danh sách category để truyền vào navbar
        List<Category> activeCategories = categoryService.getAllActiveCategory();
        model.addAttribute("categories",activeCategories);

    }


    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/signin")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/product")
    public String showProducts(Model model, @RequestParam(value = "category", defaultValue = "") String category) {

        List<Category> categories = categoryService.getAllActiveCategory();
        List<Product> products = productService.getAllActiveProduct(category);

        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        //truyền param sang để thực hiện bôi đen danh mục được chọn
        model.addAttribute("paramValue", category);


        return "product";
    }

    @GetMapping("/view_product/{id}")
    public String viewDetailProduct(@PathVariable int id, Model model) {
        Product productById = productService.findProductById(id);
        model.addAttribute("product", productById);
        return "view_product";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute UserDtls user, @RequestParam("imageFile") MultipartFile file, HttpSession session) throws IOException {

        String userImage = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        user.setProfileImage(userImage);

        UserDtls saveUser = userService.saveUser(user);

        if (!ObjectUtils.isEmpty(saveUser)) {

            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();

                File profileImageDir = new File(saveFile, "profile_images");
                // Kiểm tra và tạo thư mục nếu không tồn tại
                if (!profileImageDir.exists()) {
                    profileImageDir.mkdirs();
                }

                //Tạo ra đường dẫn tới vị trí lưu hình ảnh, bao gồm tên file của hình ảnh.
                // separator đại diện cho dấu '/'
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_images" + File.separator + file.getOriginalFilename());
                System.out.println(path);
                //Copy nội dung của file tải lên từ client và lưu nó vào đường dẫn path vừa tạo.
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("succMsg", "Register success");

        } else {
            session.setAttribute("errorMsg", "Register success");
        }

        return "redirect:/register";
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam String ch, Model model){
       List<Product> listP =  productService.searchProduct(ch);
       List<Category> categories = categoryService.getAllActiveCategory();
       model.addAttribute("products",listP);
       model.addAttribute("categories",categories);
        return "product";
    }





}
