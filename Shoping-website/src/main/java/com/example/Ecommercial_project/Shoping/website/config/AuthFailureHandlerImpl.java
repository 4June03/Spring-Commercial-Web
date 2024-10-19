package com.example.Ecommercial_project.Shoping.website.config;

import com.example.Ecommercial_project.Shoping.website.model.UserDtls;
import com.example.Ecommercial_project.Shoping.website.repository.UserRepository;
import com.example.Ecommercial_project.Shoping.website.service.UserService;
import com.example.Ecommercial_project.Shoping.website.util.AppConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

    private UserRepository userRepository;
    private UserService userService;

    @Autowired
    public AuthFailureHandlerImpl(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String email = request.getParameter("username");

        UserDtls userDtls = userRepository.findByEmail(email);

        if (userDtls.getIsEnable()){

            if (userDtls.getAccountNonLocked()){ //Nếu tài khoản chưa bị khóa

                if (userDtls.getFailedAttempt()< AppConstant.ATTEMPT_TIME){ //Số lần nhập sai < 3
                    userService.increaseFailedAttempt(userDtls); //tăng số lần nhập sai của tài khoản này
                }else { //Nhập sai quá 3 lần
                    userService.userAccountLock(userDtls);//Khóa tài khoản
                    exception = new LockedException("Your account is locked !! failed attempt "+userDtls.getFailedAttempt());
                }

            }else { //Nếu đã bị khóa

                if(userService.unlockAccountTimeExpired(userDtls)){ //Nếu hết thời gian khóa
                    exception = new LockedException("Your account is unlocked !! please try to login");
                }else {
                    exception = new LockedException("Your account is locked");
                }

            }

        }else {
            exception = new LockedException("Your account is inactive");
        }
        super.setDefaultFailureUrl("/signin?error");//Bất kì lỗi nào thì url này sẽ được nhập
        super.onAuthenticationFailure(request, response, exception);
    }
}
