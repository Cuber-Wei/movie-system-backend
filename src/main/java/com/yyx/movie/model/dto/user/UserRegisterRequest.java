package com.yyx.movie.model.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 二次确认密码
     */
    private String checkPassword;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 用户生日
     */
    private Date birthday;

    /**
     * 用户性别
     */
    private String userGender;
}
