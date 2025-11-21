package com.yyx.movie.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 电影
 *
 * @TableName movie
 */
@TableName(value = "movie")
@Data
public class Movie implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long movieId;
    /**
     * 电影名称
     */
    private String title;
    /**
     * 电影介绍
     */
    private String introduction;
    /**
     * 演员信息
     */
    private String actors;
    /**
     * 时长
     */
    private Integer duration;
    /**
     * 标签列表（json 数组）
     */
    private String tag;
    /**
     * 出版日期
     */
    private Date publishDate;
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