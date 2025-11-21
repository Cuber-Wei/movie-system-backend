package com.yyx.movie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yyx.movie.common.BaseResponse;
import com.yyx.movie.common.ErrorCode;
import com.yyx.movie.common.ResultUtils;
import com.yyx.movie.exception.BusinessException;
import com.yyx.movie.exception.ThrowUtils;
import com.yyx.movie.model.dto.collect.CollectAddRequest;
import com.yyx.movie.model.dto.collect.CollectQueryRequest;
import com.yyx.movie.model.dto.movie.MovieQueryRequest;
import com.yyx.movie.model.entity.Movie;
import com.yyx.movie.model.entity.User;
import com.yyx.movie.model.vo.MovieVO;
import com.yyx.movie.service.CollectService;
import com.yyx.movie.service.MovieService;
import com.yyx.movie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子收藏接口
 */
@RestController
@RequestMapping("/collect")
@Slf4j
public class CollectController {

    @Resource
    private CollectService collectService;

    @Resource
    private MovieService movieService;

    @Resource
    private UserService userService;

    /**
     * 收藏 / 取消收藏
     *
     * @param collectAddRequest
     * @param request
     * @return resultNum 收藏变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doCollect(@RequestBody CollectAddRequest collectAddRequest,
                                           HttpServletRequest request) {
        if (collectAddRequest == null || collectAddRequest.getMovieId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能操作
        final User loginUser = userService.getLoginUser(request);
        long movieId = collectAddRequest.getMovieId();
        int result = collectService.doCollect(movieId, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取我收藏的帖子列表
     *
     * @param movieQueryRequest
     * @param request
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<MovieVO>> listMyFavourMovieByPage(@RequestBody MovieQueryRequest movieQueryRequest,
                                                               HttpServletRequest request) {
        if (movieQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long current = movieQueryRequest.getCurrent();
        long size = movieQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Movie> moviePage = collectService.listFavourMovieByPage(new Page<>(current, size),
                movieService.getQueryWrapper(movieQueryRequest), loginUser.getUserId());
        return ResultUtils.success(movieService.getMovieVOPage(moviePage, request));
    }

    /**
     * 获取用户收藏的帖子列表
     *
     * @param collectQueryRequest
     * @param request
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<MovieVO>> listFavourMovieByPage(@RequestBody CollectQueryRequest collectQueryRequest,
                                                             HttpServletRequest request) {
        if (collectQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = collectQueryRequest.getCurrent();
        long size = collectQueryRequest.getPageSize();
        Long userId = collectQueryRequest.getUserId();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20 || userId == null, ErrorCode.PARAMS_ERROR);
        Page<Movie> moviePage = collectService.listFavourMovieByPage(new Page<>(current, size),
                movieService.getQueryWrapper(collectQueryRequest.getMovieQueryRequest()), userId);
        return ResultUtils.success(movieService.getMovieVOPage(moviePage, request));
    }
}
