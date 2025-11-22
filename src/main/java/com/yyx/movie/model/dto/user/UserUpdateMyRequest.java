package com.yyx.movie.model.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户更新个人信息请求
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 用户昵称
     */
    private String userAccount;
    /**
     * 用户头像
     */
    private String userGender;
    /**
     * 用户手机号
     */
    private String userPhone;
    /**
     * 用户生日
     */
    private Date birthDay;
}