package com.yyx.movie.model.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户更新请求
 */
@Data
public class UserUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long userId;
    /**
     * 用户名
     */
    private String userAccount;
    /**
     * 简介
     */
    private String userProfile;
    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;
    /**
     * 用户手机号
     */
    private String userPhone;
    /**
     * 用户邮箱
     */
    private String userMail;
    /**
     * 用户生日
     */
    private Date birthDay;
}