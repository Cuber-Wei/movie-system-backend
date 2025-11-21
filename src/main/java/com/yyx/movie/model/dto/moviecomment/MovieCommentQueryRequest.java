package com.yyx.movie.model.dto.moviecomment;

import com.yyx.movie.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MovieCommentQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long movieCommentId;
    /**
     * not id
     */
    private Long notMovieCommentId;
    /**
     * 搜索词
     */
    private String searchText;
    /**
     * 帖子id
     */
    private Long movieId;
    /**
     * 内容
     */
    private String content;
    /**
     * 评分
     */
    private Float score;
    /**
     * 审核状态
     */
    private Integer reviewStatus;
    /**
     * not 审核状态
     */
    private Integer notReviewStatus;
    /**
     * 创建用户 id
     */
    private Long userId;
}