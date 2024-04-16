package com.lam.usercenter.controller;

import com.lam.usercenter.common.BaseResponse;
import com.lam.usercenter.common.ErrorCode;
import com.lam.usercenter.common.ResultUtils;
import com.lam.usercenter.exception.BusinessException;
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
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        Long userId = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(userId);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @GetMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.userLogout(request));
    }

    @GetMapping("/getAllUser")
    public BaseResponse<List<User>> getAllUser(HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        return ResultUtils.success(userService.selectAll());
    }



    @PostMapping("/deleteOne")
    public BaseResponse<Boolean> deleteOne(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.removeById(id));
    }


    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATUS);
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = currentUser.getId();
        User safetyUser = userService.getSafetyUser(userService.getById(userId));
        return ResultUtils.success(safetyUser);
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
