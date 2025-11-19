package com.l0v3ch4n.oj.model.dto.postcomment;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 */
@Data
public class PostCommentAddRequest implements Serializable {

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
     * 用户 id
     */
    private Long userId;
}