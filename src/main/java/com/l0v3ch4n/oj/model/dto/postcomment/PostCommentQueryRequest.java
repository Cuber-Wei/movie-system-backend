package com.l0v3ch4n.oj.model.dto.postcomment;

import com.l0v3ch4n.oj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostCommentQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long postCommentId;
    /**
     * not id
     */
    private Long notPostCommentId;
    /**
     * 搜索词
     */
    private String searchText;
    /**
     * 帖子id
     */
    private Long postId;
    /**
     * 内容
     */
    private String content;
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