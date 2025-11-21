package com.yyx.movie.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子评论
 *
 * @TableName movie_comment
 */
@TableName(value = "movie_comment")
@Data
public class MovieComment implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long movieCommentId;
    /**
     * 创建用户 id
     */
    private Long userId;
    /**
     * 帖子 id
     */
    private Long movieId;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 审核状态（0 - 待审核、1 - 审核通过、2 - 审核未通过）
     */
    private Integer reviewStatus;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除
     */
    private Integer isDelete;
}