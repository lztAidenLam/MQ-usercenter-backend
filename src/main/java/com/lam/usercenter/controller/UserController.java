package com.lam.usercenter.controller;

import com.lam.usercenter.model.domain.request.UserLoginRequest;
import com.lam.usercenter.model.domain.request.UserRegisterRequest;
import com.lam.usercenter.model.entity.User;
import com.lam.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.lam.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.lam.usercenter.constant.UserConstant.USER_LOGIN_STATUS;

/**
 * @author AidenLam
 * @date 2024/4/12
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        return userService.userRegister(userAccount, userPassword, checkPassword);
    }

    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        User user = userService.userLogin(userAccount, userPassword, request);
        return user;
    }

    @GetMapping("/logout")
    public Integer userLogout(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return userService.userLogout(request);
    }

    @GetMapping("/getAllUser")
    public List<User> getAllUser(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return new ArrayList<>();
        }

        return userService.selectAll();
    }



    @PostMapping("/deleteOne")
    public boolean deleteOne(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return false;
        }

        if (id <= 0) {
            return false;
        }
        return userService.removeById(id);
    }


    @GetMapping("/current")
    public User getCurrentUser(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATUS);
        if (currentUser == null){
            return null;
        }
        Long userId = currentUser.getId();
        return userService.getSafetyUser(userService.getById(userId));
    }


    private boolean isAdmin(HttpServletRequest request) {
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATUS);
        if (loginUser == null){
            return false;
        } else {
            return ADMIN_ROLE.equals(loginUser.getUserRole());
        }
    }
}
