package com.l0v3ch4n.oj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.l0v3ch4n.oj.model.dto.postcomment.PostCommentQueryRequest;
import com.l0v3ch4n.oj.model.entity.PostComment;
import com.l0v3ch4n.oj.model.vo.PostCommentVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 题解服务
 */
public interface PostCommentService extends IService<PostComment> {

    /**
     * 校验
     *
     * @param postComment
     * @param add
     */
    void validPostComment(PostComment postComment, boolean add);

    /**
     * 获取查询条件
     *
     * @param postCommentQueryRequest
     * @return
     */
    QueryWrapper<PostComment> getQueryWrapper(PostCommentQueryRequest postCommentQueryRequest);

    /**
     * 从 ES 查询
     *
     * @param postCommentQueryRequest
     * @return
     */
    Page<PostComment> searchFromEs(PostCommentQueryRequest postCommentQueryRequest);

    /**
     * 获取题解封装
     *
     * @param postComment
     * @param request
     * @return
     */
    PostCommentVO getPostCommentVO(PostComment postComment, HttpServletRequest request);

    /**
     * 分页获取题解封装
     *
     * @param postCommentPage
     * @param request
     * @return
     */
    Page<PostCommentVO> getPostCommentVOPage(Page<PostComment> postCommentPage, HttpServletRequest request);
}
