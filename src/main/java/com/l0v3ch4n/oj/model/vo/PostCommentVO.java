package com.l0v3ch4n.oj.model.vo;

import com.l0v3ch4n.oj.model.entity.PostComment;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子评论视图
 */
@Data
public class PostCommentVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 评论id
     */
    private Long postCommentId;
    /**
     * 创建用户 id
     */
    private Long userId;
    /**
     * 帖子 id
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
     * id
     */
    private Long id;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 创建人信息
     */
    private UserVO user;

    /**
     * 包装类转对象
     *
     * @param postCommentVO
     * @return
     */
    public static PostComment voToObj(PostCommentVO postCommentVO) {
        if (postCommentVO == null) {
            return null;
        }
        PostComment postComment = new PostComment();
        BeanUtils.copyProperties(postCommentVO, postComment);
        return postComment;
    }

    /**
     * 对象转包装类
     *
     * @param postComment
     * @return
     */
    public static PostCommentVO objToVo(PostComment postComment) {
        if (postComment == null) {
            return null;
        }
        PostCommentVO postCommentVO = new PostCommentVO();
        BeanUtils.copyProperties(postComment, postCommentVO);
        return postCommentVO;
    }
}
