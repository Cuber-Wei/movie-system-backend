package com.yyx.movie.model.dto.movieinlist;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 */
@Data
public class MovieInListAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 电影 id
     */
    private Long movieId;
    /**
     * 榜单 id
     */
    private Long movieListId;
}