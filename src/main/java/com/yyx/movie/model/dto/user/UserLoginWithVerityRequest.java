package com.yyx.movie.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 */
@Data
public class UserLoginWithVerityRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 验证码
     */
    private String verityCode;

    /**
     * 用户邮箱
     */
    private String userMail;
}
