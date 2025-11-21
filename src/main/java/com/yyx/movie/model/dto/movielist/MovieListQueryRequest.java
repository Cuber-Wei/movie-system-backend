package com.yyx.movie.model.dto.movielist;

import com.yyx.movie.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MovieListQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long movieListId;
    /**
     * id
     */
    private Long notMovieListId;
    /**
     * 搜索词
     */
    private String searchText;
    /**
     * 标题
     */
    private String title;
    /**
     * 简介
     */
    private String listIntroduction;
    /**
     * 创建用户 id
     */
    private Long userId;
}