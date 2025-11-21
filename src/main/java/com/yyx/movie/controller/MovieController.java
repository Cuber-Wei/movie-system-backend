package com.yyx.movie.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yyx.movie.annotation.AuthCheck;
import com.yyx.movie.common.BaseResponse;
import com.yyx.movie.common.DeleteRequest;
import com.yyx.movie.common.ErrorCode;
import com.yyx.movie.common.ResultUtils;
import com.yyx.movie.constant.UserConstant;
import com.yyx.movie.exception.BusinessException;
import com.yyx.movie.exception.ThrowUtils;
import com.yyx.movie.model.dto.movie.MovieAddRequest;
import com.yyx.movie.model.dto.movie.MovieEditRequest;
import com.yyx.movie.model.dto.movie.MovieQueryRequest;
import com.yyx.movie.model.dto.movie.MovieUpdateRequest;
import com.yyx.movie.model.entity.Movie;
import com.yyx.movie.model.entity.User;
import com.yyx.movie.model.vo.MovieVO;
import com.yyx.movie.service.MovieService;
import com.yyx.movie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子接口
 */
@RestController
@RequestMapping("/movie")
@Slf4j
public class MovieController {

    @Resource
    private MovieService movieService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param movieAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addMovie(@RequestBody MovieAddRequest movieAddRequest, HttpServletRequest request) {
        if (movieAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 仅管理员可删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        Movie movie = new Movie();
        BeanUtils.copyProperties(movieAddRequest, movie);
        List<String> tag = movieAddRequest.getTag();
        if (tag != null) {
            movie.setTag(JSONUtil.toJsonStr(tag));
        }
        movieService.validMovie(movie, true);
        boolean result = movieService.save(movie);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newMovieId = movie.getMovieId();
        return ResultUtils.success(newMovieId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteMovie(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Movie oldMovie = movieService.getById(id);
        ThrowUtils.throwIf(oldMovie == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅管理员可删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = movieService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param movieUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateMovie(@RequestBody MovieUpdateRequest movieUpdateRequest) {
        if (movieUpdateRequest == null || movieUpdateRequest.getMovieId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Movie movie = new Movie();
        BeanUtils.copyProperties(movieUpdateRequest, movie);
        List<String> tag = movieUpdateRequest.getTag();
        if (tag != null) {
            movie.setTag(JSONUtil.toJsonStr(tag));
        }
        // 参数校验
        movieService.validMovie(movie, false);
        long id = movieUpdateRequest.getMovieId();
        // 判断是否存在
        Movie oldMovie = movieService.getById(id);
        ThrowUtils.throwIf(oldMovie == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = movieService.updateById(movie);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<MovieVO> getMovieVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Movie movie = movieService.getById(id);
        if (movie == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(movieService.getMovieVO(movie, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param movieQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Movie>> listMovieByPage(@RequestBody MovieQueryRequest movieQueryRequest) {
        long current = movieQueryRequest.getCurrent();
        long size = movieQueryRequest.getPageSize();
        Page<Movie> moviePage = movieService.page(new Page<>(current, size),
                movieService.getQueryWrapper(movieQueryRequest));
        return ResultUtils.success(moviePage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param movieQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<MovieVO>> listMovieVOByPage(@RequestBody MovieQueryRequest movieQueryRequest,
                                                         HttpServletRequest request) {
        long current = movieQueryRequest.getCurrent();
        long size = movieQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Movie> moviePage = movieService.page(new Page<>(current, size),
                movieService.getQueryWrapper(movieQueryRequest));
        return ResultUtils.success(movieService.getMovieVOPage(moviePage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param movieQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<MovieVO>> listMyMovieVOByPage(@RequestBody MovieQueryRequest movieQueryRequest,
                                                           HttpServletRequest request) {
        if (movieQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = movieQueryRequest.getCurrent();
        long size = movieQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Movie> moviePage = movieService.page(new Page<>(current, size),
                movieService.getQueryWrapper(movieQueryRequest));
        return ResultUtils.success(movieService.getMovieVOPage(moviePage, request));
    }

    // endregion

    /**
     * 分页搜索（从 ES 查询，封装类）
     *
     * @param movieQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/search/page/vo")
    public BaseResponse<Page<MovieVO>> searchMovieVOByPage(@RequestBody MovieQueryRequest movieQueryRequest,
                                                           HttpServletRequest request) {
        long size = movieQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Movie> moviePage = movieService.searchFromEs(movieQueryRequest);
        return ResultUtils.success(movieService.getMovieVOPage(moviePage, request));
    }

    /**
     * 编辑（用户）
     *
     * @param movieEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editMovie(@RequestBody MovieEditRequest movieEditRequest, HttpServletRequest request) {
        if (movieEditRequest == null || movieEditRequest.getMovieId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Movie movie = new Movie();
        BeanUtils.copyProperties(movieEditRequest, movie);
        List<String> tag = movieEditRequest.getTag();
        if (tag != null) {
            movie.setTag(JSONUtil.toJsonStr(tag));
        }
        // 参数校验
        movieService.validMovie(movie, false);
        User loginUser = userService.getLoginUser(request);
        long id = movieEditRequest.getMovieId();
        // 判断是否存在
        Movie oldMovie = movieService.getById(id);
        ThrowUtils.throwIf(oldMovie == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅管理员可编辑
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = movieService.updateById(movie);
        return ResultUtils.success(result);
    }

}
