package com.yyx.movie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yyx.movie.annotation.AuthCheck;
import com.yyx.movie.common.BaseResponse;
import com.yyx.movie.common.DeleteRequest;
import com.yyx.movie.common.ErrorCode;
import com.yyx.movie.common.ResultUtils;
import com.yyx.movie.constant.AuditConstant;
import com.yyx.movie.constant.UserConstant;
import com.yyx.movie.exception.BusinessException;
import com.yyx.movie.exception.ThrowUtils;
import com.yyx.movie.model.dto.moviecomment.MovieCommentAddRequest;
import com.yyx.movie.model.dto.moviecomment.MovieCommentQueryRequest;
import com.yyx.movie.model.entity.MovieComment;
import com.yyx.movie.model.entity.User;
import com.yyx.movie.model.vo.MovieCommentVO;
import com.yyx.movie.service.MovieCommentService;
import com.yyx.movie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题解接口
 */
@RestController
@RequestMapping("/movieComment")
@Slf4j
public class MovieCommentController {

    @Resource
    private MovieCommentService movieCommentService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param movieCommentAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addMovieComment(@RequestBody MovieCommentAddRequest movieCommentAddRequest, HttpServletRequest request) {
        if (movieCommentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        MovieComment movieComment = new MovieComment();
        BeanUtils.copyProperties(movieCommentAddRequest, movieComment);
        movieCommentService.validMovieComment(movieComment, true);
        User loginUser = userService.getLoginUser(request);
        movieComment.setUserId(loginUser.getUserId());
        movieComment.setReviewStatus(AuditConstant.PENDING);
        boolean result = movieCommentService.save(movieComment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newMovieCommentId = movieComment.getMovieCommentId();
        return ResultUtils.success(newMovieCommentId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteMovieComment(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        MovieComment oldMovieComment = movieCommentService.getById(id);
        ThrowUtils.throwIf(oldMovieComment == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldMovieComment.getUserId().equals(user.getUserId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = movieCommentService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<MovieCommentVO> getMovieCommentVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        MovieComment movieComment = movieCommentService.getById(id);
        if (movieComment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(movieCommentService.getMovieCommentVO(movieComment, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param movieCommentQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<MovieComment>> listMovieCommentByPage(@RequestBody MovieCommentQueryRequest movieCommentQueryRequest) {
        long current = movieCommentQueryRequest.getCurrent();
        long size = movieCommentQueryRequest.getPageSize();
        Page<MovieComment> movieCommentPage = movieCommentService.page(new Page<>(current, size),
                movieCommentService.getQueryWrapper(movieCommentQueryRequest));
        return ResultUtils.success(movieCommentPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param movieCommentQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<MovieCommentVO>> listMovieCommentVOByPage(@RequestBody MovieCommentQueryRequest movieCommentQueryRequest,
                                                                       HttpServletRequest request) {
        long current = movieCommentQueryRequest.getCurrent();
        long size = movieCommentQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<MovieComment> movieCommentPage = movieCommentService.page(new Page<>(current, size),
                movieCommentService.getQueryWrapper(movieCommentQueryRequest));
        return ResultUtils.success(movieCommentService.getMovieCommentVOPage(movieCommentPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param movieCommentQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<MovieCommentVO>> listMyMovieCommentVOByPage(@RequestBody MovieCommentQueryRequest movieCommentQueryRequest,
                                                                         HttpServletRequest request) {
        if (movieCommentQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        movieCommentQueryRequest.setUserId(loginUser.getUserId());
        long current = movieCommentQueryRequest.getCurrent();
        long size = movieCommentQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<MovieComment> movieCommentPage = movieCommentService.page(new Page<>(current, size),
                movieCommentService.getQueryWrapper(movieCommentQueryRequest));
        return ResultUtils.success(movieCommentService.getMovieCommentVOPage(movieCommentPage, request));
    }

    // endregion

    /**
     * 分页搜索（从 ES 查询，封装类）
     *
     * @param movieCommentQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/search/page/vo")
    public BaseResponse<Page<MovieCommentVO>> searchMovieCommentVOByPage(@RequestBody MovieCommentQueryRequest movieCommentQueryRequest,
                                                                         HttpServletRequest request) {
        long size = movieCommentQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<MovieComment> movieCommentPage = movieCommentService.searchFromEs(movieCommentQueryRequest);
        return ResultUtils.success(movieCommentService.getMovieCommentVOPage(movieCommentPage, request));
    }

}
