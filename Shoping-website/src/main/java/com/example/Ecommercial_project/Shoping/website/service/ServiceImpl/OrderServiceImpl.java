package com.example.Ecommercial_project.Shoping.website.service.ServiceImpl;

import com.example.Ecommercial_project.Shoping.website.model.Cart;
import com.example.Ecommercial_project.Shoping.website.model.OrderAddress;
import com.example.Ecommercial_project.Shoping.website.model.OrderRequest;
import com.example.Ecommercial_project.Shoping.website.model.ProductOrder;
import com.example.Ecommercial_project.Shoping.website.repository.CartRepository;
import com.example.Ecommercial_project.Shoping.website.repository.ProductOrderRepository;
import com.example.Ecommercial_project.Shoping.website.service.OrderService;
import com.example.Ecommercial_project.Shoping.website.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public ProductOrder saveOrder(Integer userId, OrderRequest orderRequest) {
        //Lay ra danh sach co trong cart bang userId
        List<Cart> carts = cartRepository.findByUserId(userId);

        for (Cart cart : carts) {
            ProductOrder order = new ProductOrder();
            //Set random ID
            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(new Date());
            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getDiscountPrice());
            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());
            order.setStatus(OrderStatus.IN_PROGRESS.name());
            order.setPaymentType(orderRequest.getPaymentType());

            OrderAddress address = new OrderAddress();
            address.setFirstName(orderRequest.getFirstName());
            address.setLastName(orderRequest.getLastName());
            address.setEmaill(orderRequest.getEmaill());
            address.setMobileNo(orderRequest.getMobileNo());
            address.setAddress(orderRequest.getAddress());
            address.setCity(orderRequest.getCity());
            address.setState(orderRequest.getState());
            address.setPincode(orderRequest.getPincode());

            order.setOrderAddress(address);

            productOrderRepository.save(order);

        }

        return null;

    }
}
