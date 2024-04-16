package com.lam.usercenter.model.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 用户表(User)表实体类
 *
 * @author makejava
 * @since 2024-04-12 10:54:59
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class User  {
    //id@TableId
    private Long id;

    //账号
    private String userAccount;
    //密码
    private String userPassword;
    //微信开放平台id
    private String unionId;
    //公众号openId
    private String mpOpenId;
    //用户昵称
    @TableField(value = "userName")
    private String username;
    //用户头像
    private String userAvatar;
    //用户简介
    private String userprofile;
    //用户角色：user/admin/ban
    private String userRole;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
    //是否删除
    @TableLogic
    private Integer isDelete;



}

