package com.yyx.movie.model.vo;

import com.yyx.movie.model.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户视图（脱敏）
 */
@Data
public class UserVO implements Serializable {

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
     * 用户邮箱
     */
    private String userMail;
    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 对象转包装类
     *
     * @param user
     * @return
     */
    public static UserVO objToVo(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        //手机号、邮箱脱敏(中间部分打*)
        String phone = user.getUserPhone();
        userVO.setUserPhone(phone.substring(0, 3) + "****" + phone.substring(phone.length() - 3));
        String mail = user.getUserMail();
        String[] parts = mail.split("@");
        userVO.setUserMail(parts[0].substring(0, 3) + "****@" + parts[1]);
        return userVO;
    }
}