package com.yyx.movie.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yyx.movie.model.dto.movie.MovieQueryRequest;
import com.yyx.movie.model.entity.Movie;
import com.yyx.movie.model.vo.MovieVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author weichenghao
 * @description 针对表【movie(电影)】的数据库操作Service
 * @createDate 2025-11-21 16:44:38
 */
public interface MovieService extends IService<Movie> {

    /**
     * 校验
     *
     * @param movie
     * @param add
     */
    void validMovie(Movie movie, boolean add);

    /**
     * 获取查询条件
     *
     * @param movieQueryRequest
     * @return
     */
    QueryWrapper<Movie> getQueryWrapper(MovieQueryRequest movieQueryRequest);

    /**
     * 从 ES 查询
     *
     * @param movieQueryRequest
     * @return
     */
    Page<Movie> searchFromEs(MovieQueryRequest movieQueryRequest);

    /**
     * 获取帖子封装
     *
     * @param movie
     * @param request
     * @return
     */
    MovieVO getMovieVO(Movie movie, HttpServletRequest request);

    /**
     * 分页获取帖子封装
     *
     * @param moviePage
     * @param request
     * @return
     */
    Page<MovieVO> getMovieVOPage(Page<Movie> moviePage, HttpServletRequest request);
}
