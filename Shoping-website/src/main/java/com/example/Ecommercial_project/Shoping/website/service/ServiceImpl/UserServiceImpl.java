package com.example.Ecommercial_project.Shoping.website.service.ServiceImpl;

import com.example.Ecommercial_project.Shoping.website.model.UserDtls;
import com.example.Ecommercial_project.Shoping.website.repository.UserRepository;
import com.example.Ecommercial_project.Shoping.website.service.UserService;
import com.example.Ecommercial_project.Shoping.website.util.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Autowired
//    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//    }


    @Override
    public UserDtls saveUser(UserDtls user) {

        user.setRole("ROLE_USER");  //Set role mặc định cho user mới là ROLE_USER
        user.setIsEnable(true); //Set user mởi tạo là enable
        user.setAccountNonLocked(true); //Set tài khoản không bị khóa = true
        user.setFailedAttempt(0); //set lần dăng nhập sai =0
        user.setLockTime(null);

        String encodePassword = passwordEncoder.encode(user.getPassword()); //Mã hóa mật khẩu
        user.setPassword(encodePassword);

        //Lưu vào db
        UserDtls savedUser = userRepository.save(user);
        return savedUser;
    }

    @Override
    public UserDtls getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDtls> getAllUsers(String role) {
        return userRepository.findByRole(role);
    }

    @Override
    public Boolean updateAccountStatus(Integer id, Boolean status) {
        Optional<UserDtls> userById = userRepository.findById(id);

        if (userById.isPresent()) {
            UserDtls userDtls = userById.get();
            userDtls.setIsEnable(status);
            userRepository.save(userDtls);
            return true;
        }
        return false;
    }

    @Override
    public void increaseFailedAttempt(UserDtls user) {
        int attempt = user.getFailedAttempt() + 1;
        user.setFailedAttempt(attempt);
        userRepository.save(user);
    }

    @Override
    public void userAccountLock(UserDtls user) {
        //khóa tài khoản
        user.setAccountNonLocked(false);
        //thời gian bắt đầu khóa
        user.setLockTime(new Date());
        userRepository.save(user);
    }

    @Override
    public boolean unlockAccountTimeExpired(UserDtls user) {
        long lockTime = user.getLockTime().getTime();

        //thời gian tài khoản được mở khóa = tg bắt đầu+tgian khóa
        //ví dụ: 6h khóa, tgian khóa 30p => 6h+30
        long unlockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;

        long currentTime = System.currentTimeMillis();

        if (unlockTime < currentTime) {
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0); //reset lần nhập sai
            user.setLockTime(null);
            userRepository.save(user);

            return true;
        }

        return false;
    }

    @Override
    public void resetAttempt(int userId) {

    }
}
