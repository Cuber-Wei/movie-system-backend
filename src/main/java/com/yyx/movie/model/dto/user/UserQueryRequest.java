package com.yyx.movie.model.dto.user;

import com.yyx.movie.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
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
     * 简介
     */
    private String userGender;
    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;
    /**
     * 用户手机号
     */
    private String userPhone;
    /**
     * 用户生日
     */
    private Date birthDay;
}