package com.l0v3ch4n.oj.model.vo;

import cn.hutool.json.JSONUtil;
import com.l0v3ch4n.oj.model.entity.WriteUp;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子视图
 */
@Data
public class WriteUpVO implements Serializable {

    /**
     * 题解id
     */
    private Long writeUpId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 审核状态
     */
    private Integer reviewStatus;

    /**
     * id
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 标签列表
     */
    private List<String> tag;

    /**
     * 创建人信息
     */
    private UserVO user;

    /**
     * 包装类转对象
     *
     * @param writeUpVO
     * @return
     */
    public static WriteUp voToObj(WriteUpVO writeUpVO) {
        if (writeUpVO == null) {
            return null;
        }
        WriteUp writeUp = new WriteUp();
        BeanUtils.copyProperties(writeUpVO, writeUp);
        List<String> tagList = writeUpVO.getTag();
        writeUp.setTag(JSONUtil.toJsonStr(tagList));
        return writeUp;
    }

    /**
     * 对象转包装类
     *
     * @param writeUp
     * @return
     */
    public static WriteUpVO objToVo(WriteUp writeUp) {
        if (writeUp == null) {
            return null;
        }
        WriteUpVO writeUpVO = new WriteUpVO();
        BeanUtils.copyProperties(writeUp, writeUpVO);
        writeUpVO.setTag(JSONUtil.toList(writeUp.getTag(), String.class));
        return writeUpVO;
    }

    private static final long serialVersionUID = 1L;
}
