package com.yyx.movie.model.dto.moviecomment;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 */
@Data
public class MovieCommentAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 帖子id
     */
    private Long postId;
    /**
     * 内容
     */
    private String content;
    /**
     * 评分
     */
    private Float score;
    /**
     * 用户 id
     */
    private Long userId;
}