package com.yyx.movie.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yyx.movie.model.dto.moviecomment.MovieCommentQueryRequest;
import com.yyx.movie.model.entity.MovieComment;
import com.yyx.movie.model.vo.MovieCommentVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 题解服务
 */
public interface MovieCommentService extends IService<MovieComment> {

    /**
     * 校验
     *
     * @param movieComment
     * @param add
     */
    void validMovieComment(MovieComment movieComment, boolean add);

    /**
     * 获取查询条件
     *
     * @param movieCommentQueryRequest
     * @return
     */
    QueryWrapper<MovieComment> getQueryWrapper(MovieCommentQueryRequest movieCommentQueryRequest);

    /**
     * 从 ES 查询
     *
     * @param movieCommentQueryRequest
     * @return
     */
    Page<MovieComment> searchFromEs(MovieCommentQueryRequest movieCommentQueryRequest);

    /**
     * 获取题解封装
     *
     * @param movieComment
     * @param request
     * @return
     */
    MovieCommentVO getMovieCommentVO(MovieComment movieComment, HttpServletRequest request);

    /**
     * 分页获取题解封装
     *
     * @param movieCommentPage
     * @param request
     * @return
     */
    Page<MovieCommentVO> getMovieCommentVOPage(Page<MovieComment> movieCommentPage, HttpServletRequest request);
}
