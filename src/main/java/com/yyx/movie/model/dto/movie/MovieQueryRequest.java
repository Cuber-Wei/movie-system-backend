package com.yyx.movie.model.dto.movie;

import com.yyx.movie.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MovieQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long movieId;
    /**
     * id
     */
    private Long notMovieId;
    /**
     * 搜索词
     */
    private String searchText;
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
    /**
     * 至少有一个标签
     */
    private List<String> orTag;
}