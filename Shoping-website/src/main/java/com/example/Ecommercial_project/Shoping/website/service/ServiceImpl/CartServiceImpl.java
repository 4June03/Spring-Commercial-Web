package com.example.Ecommercial_project.Shoping.website.service.ServiceImpl;

import com.example.Ecommercial_project.Shoping.website.model.Cart;
import com.example.Ecommercial_project.Shoping.website.model.Product;
import com.example.Ecommercial_project.Shoping.website.model.UserDtls;
import com.example.Ecommercial_project.Shoping.website.repository.CartRepository;
import com.example.Ecommercial_project.Shoping.website.repository.ProductRepository;
import com.example.Ecommercial_project.Shoping.website.repository.UserRepository;
import com.example.Ecommercial_project.Shoping.website.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    private CartRepository cartRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Cart saveCart(Integer productId, Integer userId) {
        UserDtls userDtls = userRepository.findById(userId).get();
        Product product = productRepository.findById(productId).get();

        Cart cartStatus = cartRepository.findByUserIdAndProductId(userId, productId);

        Cart cart = null;

        if (ObjectUtils.isEmpty(cartStatus)){ //Nếu user này chưa có giỏ hàng nào
            cart = new Cart();
            cart.setProduct(product);
            cart.setUser(userDtls);
            cart.setQuantity(1);
            cart.setTotalPrice(1*product.getDiscountPrice());
        }else {//User có cart rồi thì set bằng cart tìm được bằng Id
            cart = cartStatus;
            cart.setQuantity(cart.getQuantity()+1);
            cart.setTotalPrice(cart.getQuantity()*cart.getProduct().getDiscountPrice());
        }

       Cart savedCart =  cartRepository.save(cart);

        return savedCart;
    }

    @Override
    public List<Cart> getCartsByUser(Integer userId) {
        return List.of();
    }
}
