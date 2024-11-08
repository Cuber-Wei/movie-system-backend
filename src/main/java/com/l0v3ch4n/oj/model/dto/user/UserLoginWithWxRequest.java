package com.l0v3ch4n.oj.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 */
@Data
public class UserLoginWithWxRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 开放平台id
     */
    private String unionId;

    /**
     * 公众号openId
     */
    private String mpOpenId;
}
