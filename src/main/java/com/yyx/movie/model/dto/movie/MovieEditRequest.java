package com.yyx.movie.model.dto.movie;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 编辑请求
 */
@Data
public class MovieEditRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long movieId;
    /**
     * 标题
     */
    private String title;
    /**
     * 介绍
     */
    private String introduction;
    /**
     * 演员
     */
    private String actors;
    /**
     * 时长
     */
    private Integer duration;
    /**
     * 出版日期
     */
    private Date publishDate;
    /**
     * 标签列表
     */
    private List<String> tag;
}