package com.yyx.movie.model.dto.movieinlist;

import com.yyx.movie.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MovieInListQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long movieInListId;
    /**
     * not id
     */
    private Long notMovieInListId;
    /**
     * 搜索词
     */
    private String searchText;
    /**
     * 帖子id
     */
    private Long movieId;
    /**
     * 创建用户 id
     */
    private Long movieListId;
}