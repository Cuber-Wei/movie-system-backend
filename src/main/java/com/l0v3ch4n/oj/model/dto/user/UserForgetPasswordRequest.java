package com.l0v3ch4n.oj.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户重置密码请求
 */
@Data
public class UserForgetPasswordRequest implements Serializable {
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
     * 确认密码
     */
    private String checkPassword;
    /**
     * 邮箱
     */
    private String userMail;

}
