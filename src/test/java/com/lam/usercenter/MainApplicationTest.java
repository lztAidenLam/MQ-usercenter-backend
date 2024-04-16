package com.lam.usercenter;

import com.lam.usercenter.mapper.UserMapper;
import com.lam.usercenter.model.entity.User;
import com.lam.usercenter.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author AidenLam
 * @date 2024/4/12
 */

@SpringBootTest
public class MainApplicationTest {

    @Autowired
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Test
    public void testLogicDelete() {
        boolean b = userService.removeById(1);
        System.out.println(b);
    }


    @Test
    public void testMBP() {
        List<User> userList = userMapper.selectList(null);
        userList.forEach(System.out::println);
        System.out.println("=================-------==================");
        System.out.println(userList.size());
    }

    @Test
    public void testUserRegister() {
        Long result = userService.userRegister("aidenlam", "123446689", "123446689");
        System.out.println(result);

//        User user = userService.getById(result);
//        if (user == null) {
//            System.out.println("无此用户");
//        }
//        System.out.println(user.getUserAccount());

    }

}
