package com.yyx.movie.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 已登录用户视图（脱敏）
 **/
@Data
public class LoginUserVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long userId;
    /**
     * 用户昵称
     */
    private String userAccount;
    /**
     * 用户性别
     */
    private String userGender;
    /**
     * 用户生日
     */
    private Date birthDay;
    /**
     * 用户手机号
     */
    private String userPhone;
    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}