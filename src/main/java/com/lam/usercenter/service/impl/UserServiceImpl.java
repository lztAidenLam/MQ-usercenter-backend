package com.lam.usercenter.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lam.usercenter.mapper.UserMapper;
import com.lam.usercenter.model.entity.User;
import com.lam.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2024-04-12 10:55:02
 */
@Service("userService")
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final String SALT = "checkOne";

    private static final String USER_LOGIN_STATUS = "userLoginStatus";


    @Override
    public Long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 校验是否符合传入参数是否符合要求
        // 密码加密
        // 向数据库插入数据
        /*
        1. 非空
        2. 账户长度不小于4位
        3. 密码长度不小于8位
        4. 账户不能重复
        5. 账户不包含特殊字符
        6. 密码和校验秘密相同
         */
        // todo 统一返回异常信息
        if (StrUtil.isAllBlank(userAccount, userPassword, checkPassword)) {
            return (long) -1;
        }
        if (userAccount.length() <= 4){
            return (long) -1;
        }
        if (userPassword.length() < 8) {
            return (long) -1;
        }
        String validRule = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%…… &*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validRule).matcher(userAccount);
        if (matcher.find()) {
            return (long) -1;
        }
        if (!userPassword.equals(checkPassword)){
            return (long) -1;
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            return (long) -1;
        }

        // 密码加密
        String verifyPassword = DigestUtils.md5DigestAsHex((SALT + userPassword)
                .getBytes(StandardCharsets.UTF_8));

        // 存入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(verifyPassword);
        this.save(user);
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 检验用户账号和密码是否合法
        // 跟数据库校验账号密码是否输入正确
        // 用户信息脱敏，返回用户信息
        /*
        1. 非空
        2. 账户长度不小于4位
        3. 密码长度不小于8位
        4. 账户不包含特殊字符
         */
        // todo 统一返回异常信息
        if (StrUtil.isAllBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() <= 4){
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        String validRule = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%…… &*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validRule).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }

        // 跟数据库校验账号密码是否输入正确
        String encoderPassword = DigestUtils.md5DigestAsHex((SALT + userPassword)
                .getBytes(StandardCharsets.UTF_8));
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        queryWrapper.eq(User::getUserPassword, encoderPassword);
        User user = this.getOne(queryWrapper);
        if (user == null) {
            log.info("user login failed, userAccount or userPassword is wrong!");
            return null;
        }

        // 用户脱敏，返回用户信息
        User safetyUser = getSafetyUser(user);

        // 记录用户的登录态（session),将其存在服务器上
        request.getSession().setAttribute(USER_LOGIN_STATUS, safetyUser); //todo
        return safetyUser;
    }

    @Override
    public List<User> selectAll() {
        List<User> userList = this.list();
//        ArrayList<User> userListVo = new ArrayList<>();
//        for (User user : userList) {
//            userListVo.add(getSafetyUser(user));
//        }

        return userList.stream().map(user -> getSafetyUser(user)).collect(Collectors.toList());
    }

    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null; //todo
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setUnionId(originUser.getUnionId());
        safetyUser.setMpOpenId(originUser.getMpOpenId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAvatar(originUser.getUserAvatar());
        safetyUser.setUserprofile(originUser.getUserprofile());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setIsDelete(originUser.getIsDelete());
        return safetyUser;
    }

    @Override
    public Integer userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATUS);
        return 1;
    }


}

