package com.yyx.movie.model.dto.collect;

import com.yyx.movie.common.PageRequest;
import com.yyx.movie.model.dto.movie.MovieQueryRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 帖子收藏查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CollectQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 帖子查询请求
     */
    private MovieQueryRequest movieQueryRequest;
    /**
     * 用户 id
     */
    private Long userId;
}