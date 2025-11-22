package com.yyx.movie.model.dto.movielist;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑请求
 */
@Data
public class MovieListEditRequest implements Serializable {

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
    private String listIntroduction;
    /**
     * 创建用户 id
     */
    private Long userId;
}