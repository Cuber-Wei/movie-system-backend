package com.yyx.movie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yyx.movie.annotation.AuthCheck;
import com.yyx.movie.common.BaseResponse;
import com.yyx.movie.common.DeleteRequest;
import com.yyx.movie.common.ErrorCode;
import com.yyx.movie.common.ResultUtils;
import com.yyx.movie.constant.UserConstant;
import com.yyx.movie.exception.BusinessException;
import com.yyx.movie.exception.ThrowUtils;
import com.yyx.movie.model.dto.movieinlist.MovieInListAddRequest;
import com.yyx.movie.model.dto.movieinlist.MovieInListQueryRequest;
import com.yyx.movie.model.dto.movielist.MovieListAddRequest;
import com.yyx.movie.model.dto.movielist.MovieListEditRequest;
import com.yyx.movie.model.dto.movielist.MovieListQueryRequest;
import com.yyx.movie.model.dto.movielist.MovieListUpdateRequest;
import com.yyx.movie.model.entity.MovieInList;
import com.yyx.movie.model.entity.MovieList;
import com.yyx.movie.model.entity.User;
import com.yyx.movie.model.vo.MovieListVO;
import com.yyx.movie.model.vo.MovieVO;
import com.yyx.movie.service.MovieInListService;
import com.yyx.movie.service.MovieListService;
import com.yyx.movie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子接口
 */
@RestController
@RequestMapping("/movie_list")
@Slf4j
public class MovieListController {

    @Resource
    private MovieListService movieListService;

    @Resource
    private UserService userService;
    @Resource
    private MovieInListService movieInListService;

    // region 增删改查

    /**
     * 创建
     *
     * @param movieListAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addMovieListList(@RequestBody MovieListAddRequest movieListAddRequest, HttpServletRequest request) {
        if (movieListAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 仅管理员可添加
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        MovieList movieList = new MovieList();
        BeanUtils.copyProperties(movieListAddRequest, movieList);
        movieListService.validMovieList(movieList, true);
        boolean result = movieListService.save(movieList);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newMovieListId = movieList.getMovieListId();
        return ResultUtils.success(newMovieListId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteMovieList(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        MovieList oldMovieList = movieListService.getById(id);
        ThrowUtils.throwIf(oldMovieList == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅管理员可删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = movieListService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param movieListUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateMovieList(@RequestBody MovieListUpdateRequest movieListUpdateRequest) {
        if (movieListUpdateRequest == null || movieListUpdateRequest.getMovieListId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        MovieList movieList = new MovieList();
        BeanUtils.copyProperties(movieListUpdateRequest, movieList);
        // 判断是否存在
        MovieList oldMovieList = movieListService.getById(movieListUpdateRequest.getMovieListId());
        ThrowUtils.throwIf(oldMovieList == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = movieListService.updateById(movieList);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<MovieListVO> getMovieListVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        MovieList movieList = movieListService.getById(id);
        if (movieList == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(movieListService.getMovieListVO(movieList));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param movieListQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<MovieList>> listMovieListByPage(@RequestBody MovieListQueryRequest movieListQueryRequest) {
        long current = movieListQueryRequest.getCurrent();
        long size = movieListQueryRequest.getPageSize();
        Page<MovieList> movieListPage = movieListService.page(new Page<>(current, size),
                movieListService.getQueryWrapper(movieListQueryRequest));
        return ResultUtils.success(movieListPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param movieListQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<MovieListVO>> listMovieListVOByPage(@RequestBody MovieListQueryRequest movieListQueryRequest,
                                                                 HttpServletRequest request) {
        long current = movieListQueryRequest.getCurrent();
        long size = movieListQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<MovieList> movieListPage = movieListService.page(new Page<>(current, size),
                movieListService.getQueryWrapper(movieListQueryRequest));
        return ResultUtils.success(movieListService.getMovieListVOPage(movieListPage));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param movieListQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<MovieListVO>> listMyMovieListVOByPage(@RequestBody MovieListQueryRequest movieListQueryRequest,
                                                                   HttpServletRequest request) {
        if (movieListQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = movieListQueryRequest.getCurrent();
        long size = movieListQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<MovieList> movieListPage = movieListService.page(new Page<>(current, size),
                movieListService.getQueryWrapper(movieListQueryRequest));
        return ResultUtils.success(movieListService.getMovieListVOPage(movieListPage));
    }

    // endregion

    /**
     * 分页搜索（从 ES 查询，封装类）
     *
     * @param movieListQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/search/page/vo")
    public BaseResponse<Page<MovieListVO>> searchMovieListVOByPage(@RequestBody MovieListQueryRequest movieListQueryRequest,
                                                                   HttpServletRequest request) {
        long size = movieListQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<MovieList> movieListPage = movieListService.searchFromEs(movieListQueryRequest);
        return ResultUtils.success(movieListService.getMovieListVOPage(movieListPage));
    }

    /**
     * 编辑（用户）
     *
     * @param movieListEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editMovieList(@RequestBody MovieListEditRequest movieListEditRequest, HttpServletRequest request) {
        if (movieListEditRequest == null || movieListEditRequest.getMovieListId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        MovieList movieList = new MovieList();
        BeanUtils.copyProperties(movieListEditRequest, movieList);
        // 参数校验
        movieListService.validMovieList(movieList, false);
        User loginUser = userService.getLoginUser(request);
        long id = movieListEditRequest.getMovieListId();
        // 判断是否存在
        MovieList oldMovieList = movieListService.getById(id);
        ThrowUtils.throwIf(oldMovieList == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅管理员可编辑
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = movieListService.updateById(movieList);
        return ResultUtils.success(result);
    }

    /**
     * 添加电影到榜单
     *
     * @param movieInListAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add/movie/to/movie_list")
    public BaseResponse<MovieListVO> addMovieToList(@RequestBody MovieInListAddRequest movieInListAddRequest, HttpServletRequest request) {
        if (movieInListAddRequest == null || movieInListAddRequest.getMovieId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        MovieListVO res = movieListService.addMovieToList(movieInListAddRequest.getMovieListId(), movieInListAddRequest.getMovieId());
        if (res == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        } else {
            return ResultUtils.success(res);
        }
    }

    /**
     * 分页查询当前榜单中的电影
     */
    @PostMapping("/list/inlist/page/vo")
    public BaseResponse<Page<MovieVO>> listMovieInListVOByPage(@RequestBody MovieInListQueryRequest movieInListQueryRequest,
                                                               HttpServletRequest request) {
        if (movieInListQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = movieInListQueryRequest.getCurrent();
        long size = movieInListQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<MovieInList> movieInListPage = movieInListService.page(new Page<>(current, size),
                movieInListService.getQueryWrapper(movieInListQueryRequest));
        return ResultUtils.success(movieInListService.getMovieInListPage(movieInListPage));
    }

}
