package com.example.Ecommercial_project.Shoping.website.controller;

import com.example.Ecommercial_project.Shoping.website.model.Cart;
import com.example.Ecommercial_project.Shoping.website.model.Category;
import com.example.Ecommercial_project.Shoping.website.model.UserDtls;
import com.example.Ecommercial_project.Shoping.website.service.CartService;
import com.example.Ecommercial_project.Shoping.website.service.CategoryService;
import com.example.Ecommercial_project.Shoping.website.service.ProductService;
import com.example.Ecommercial_project.Shoping.website.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private CategoryService categoryService;
    private ProductService productService;
    private UserService userService;
    private CartService cartService;

    @Autowired
    public UserController(CategoryService categoryService, ProductService productService, UserService userService, CartService cartService) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.userService = userService;
        this.cartService = cartService;
    }

    @GetMapping("/")
    public String home(){
        return "user/home";
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


    @GetMapping("/addToCart")
    public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session){
        Cart savedCart = cartService.saveCart(pid, uid);

        if(ObjectUtils.isEmpty(savedCart)){
            session.setAttribute("errorMsg", "Add to cart failed");
        }else {
            session.setAttribute("succMsg", "Add to cart success");
        }

        return "redirect:/view_product/"+pid;
    }

    @GetMapping("/cart")
    public String loadCartPage(Principal p,Model model, HttpSession session){

        UserDtls userDtls = getLoggedInUserDetails(p);
        List<Cart> carts = cartService.getCartsByUser(userDtls.getId());
        model.addAttribute("carts",carts);

        if(carts.size()>0){
            Double totalOrderPrice = carts.get(carts.size()-1).getTotalOrderPrice();
            model.addAttribute("totalOrderPrice",totalOrderPrice);
        }

        return "/user/cart";
    }

    //Phương thức trả về user đã đăng nhập
    private UserDtls getLoggedInUserDetails(Principal p){
        String email = p.getName();
        UserDtls userDtls = userService.getUserByEmail(email);
        return userDtls;
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid){

        cartService.updateQuantity(sy,cid);
        return "redirect:/user/cart";
    }

}
