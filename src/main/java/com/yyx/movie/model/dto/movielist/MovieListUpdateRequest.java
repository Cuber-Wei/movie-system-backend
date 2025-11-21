package com.yyx.movie.model.dto.movielist;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求
 */
@Data
public class MovieListUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long movieListId;
    /**
     * 标题
     */
    private String title;
    /**
     * 简介
     */
    private String introduction;
    /**
     * 创建用户 id
     */
    private Long userId;
}