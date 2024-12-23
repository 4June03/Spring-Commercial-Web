package com.example.Ecommercial_project.Shoping.website.controller;

import com.example.Ecommercial_project.Shoping.website.model.Cart;
import com.example.Ecommercial_project.Shoping.website.model.Category;
import com.example.Ecommercial_project.Shoping.website.model.OrderRequest;
import com.example.Ecommercial_project.Shoping.website.model.UserDtls;
import com.example.Ecommercial_project.Shoping.website.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/user")
public class UserController {

    private CategoryService categoryService;
    private ProductService productService;
    private UserService userService;
    private CartService cartService;
    private OrderService orderService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(CategoryService categoryService, ProductService productService, UserService userService, CartService cartService, OrderService orderService, PasswordEncoder passwordEncoder) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.userService = userService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.passwordEncoder = passwordEncoder;
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

    @GetMapping("/orders")
    public String orderPage(Principal p, Model model){
        UserDtls userDtls = getLoggedInUserDetails(p);
        List<Cart> carts = cartService.getCartsByUser(userDtls.getId());
        model.addAttribute("carts",carts);

        if(carts.size()>0){
            Double deliverFee = 25000.0; //phí ship
            Double tax = carts.get(carts.size()-1).getTotalOrderPrice()*(5.0/100); //Thuế 5%
            Double orderPrice = carts.get(carts.size()-1).getTotalOrderPrice();//tổng chưa thuế + ship
            Double totalOrderPrice = carts.get(carts.size()-1).getTotalOrderPrice()+tax+deliverFee; //tổng thuế + ship
            model.addAttribute("totalOrderPrice",totalOrderPrice);
            model.addAttribute("orderPrice",orderPrice);
            model.addAttribute("tax",tax);
        }

        return "/user/order";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest request, Principal p){
        //System.out.println(request);
        UserDtls user = getLoggedInUserDetails(p);
        orderService.saveOrder(user.getId(),request);
        return "redirect:/user/success";
    }

    @GetMapping("/success")
    public String loadSuccess(){
        return "/user/success";
    }

    @GetMapping("/profile")
    public String profile(){
        return "/user/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile file){
        userService.updateUserProfile(user, file);

        return "redirect:/user/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword, @RequestParam String currentPass, Principal p, HttpSession session){
        UserDtls user = getLoggedInUserDetails(p);
        Boolean matches = passwordEncoder.matches(currentPass, user.getPassword());//Check mật khẩu hiện tại nhập vào với mật khẩu có chính xác không

        if(matches){
            user.setPassword(passwordEncoder.encode(newPassword));

            UserDtls updateUser = userService.updateUser(user);

            if(ObjectUtils.isEmpty(updateUser)){
                session.setAttribute("errorMsg", "Password is not changed");
            }else {
                session.setAttribute("succMsg", "Password is successfully changed");
            }

        }else {
            session.setAttribute("errorMsg", "Current Password incorrect");
        }

        return "redirect:/user/profile";
    }

}
