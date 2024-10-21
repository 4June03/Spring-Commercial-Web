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

import java.util.ArrayList;
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
        List<Cart> carts = cartRepository.findByUserId(userId);

        Double totalOrderPrice = 0.0;

        List<Cart> updateCarts = new ArrayList<>();

        for (Cart c: carts){
            //Tính tổng tiền của từng product với số lượng trong cart
            Double totalPrice = (c.getProduct().getDiscountPrice()*c.getQuantity());
            c.setTotalPrice(totalPrice);

            //Tổng tiền của giỏ hàng
            totalOrderPrice+= c.getTotalPrice();
            c.setTotalOrderPrice(totalOrderPrice);
            updateCarts.add(c);

        }

        return updateCarts;
    }

    @Override
    public Integer getCountCart(Integer userId) {
        Integer countByUserId = cartRepository.countByUserId(userId);

        return countByUserId;
    }

    @Override
    public void updateQuantity(String sy, Integer cid) {

        Cart cartById = cartRepository.findById(cid).get();
        int updateQuantity;
        if (sy.equalsIgnoreCase("in")){
            updateQuantity = cartById.getQuantity()+1;
            cartById.setQuantity(updateQuantity);
            cartRepository.save(cartById);

        }else {
            updateQuantity = cartById.getQuantity()-1;

            //Nếu giảm số lượng = 0 hoặc nhỏ hơn thì xóa khỏi cart
            if (updateQuantity<=0){
                cartRepository.delete(cartById);
            }else {
                cartById.setQuantity(updateQuantity);
                cartRepository.save(cartById);
            }
        }

    }
}
