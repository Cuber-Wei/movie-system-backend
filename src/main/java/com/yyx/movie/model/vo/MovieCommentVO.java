package com.yyx.movie.model.vo;

import com.yyx.movie.model.entity.MovieComment;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子评论视图
 */
@Data
public class MovieCommentVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 评论id
     */
    private Long movieCommentId;
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
     * @param movieCommentVO
     * @return
     */
    public static MovieComment voToObj(MovieCommentVO movieCommentVO) {
        if (movieCommentVO == null) {
            return null;
        }
        MovieComment movieComment = new MovieComment();
        BeanUtils.copyProperties(movieCommentVO, movieComment);
        return movieComment;
    }

    /**
     * 对象转包装类
     *
     * @param movieComment
     * @return
     */
    public static MovieCommentVO objToVo(MovieComment movieComment) {
        if (movieComment == null) {
            return null;
        }
        MovieCommentVO movieCommentVO = new MovieCommentVO();
        BeanUtils.copyProperties(movieComment, movieCommentVO);
        return movieCommentVO;
    }
}
