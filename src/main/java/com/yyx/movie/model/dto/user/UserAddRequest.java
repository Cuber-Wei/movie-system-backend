package com.yyx.movie.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户创建请求
 */
@Data
public class UserAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 账号
     */
    private String userAccount;
    /**
     * 用户角色: user, admin
     */
    private String userRole;
    /**
     * 用户手机号
     */
    private String userPhone;
    /**
     * 生日
     */
    private String birthDay;
    /**
     * 用户性别
     */
    private String userGender;
}