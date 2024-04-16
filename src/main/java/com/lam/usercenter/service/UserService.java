package com.lam.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lam.usercenter.model.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 用户表(User)表服务接口
 *
 * @author makejava
 * @since 2024-04-12 10:55:01
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param userAccount 用户传输账号
     * @param userPassword 用户传输密码
     * @param checkPassword 用户传输校验密码
     * @return
     */
    Long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount 用户传输账号
     * @param userPassword 用户传输密码
     * @return
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);


    List<User> selectAll();

    /**
     * 用户脱敏
     * @param originUser 原始用户
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 退出登录
     * @param request
     * @return
     */
    Integer userLogout(HttpServletRequest request);
}
