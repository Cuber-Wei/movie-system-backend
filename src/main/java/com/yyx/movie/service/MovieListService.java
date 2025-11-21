package com.yyx.movie.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yyx.movie.model.dto.movielist.MovieListQueryRequest;
import com.yyx.movie.model.entity.MovieList;
import com.yyx.movie.model.vo.MovieListVO;

/**
 * @author weichenghao
 * @description 针对表【movielist(电影)】的数据库操作Service
 * @createDate 2025-11-21 16:44:38
 */
public interface MovieListService extends IService<MovieList> {

    /**
     * 校验
     *
     * @param movieList
     * @param add
     */
    void validMovieList(MovieList movieList, boolean add);

    /**
     * 添加电影到榜单
     *
     * @param movieListId
     * @param movieId
     * @return
     */
    MovieListVO addMovieToList(Long movieListId, Long movieId);

    /**
     * 获取查询条件
     *
     * @param movieListQueryRequest
     * @return
     */
    QueryWrapper<MovieList> getQueryWrapper(MovieListQueryRequest movieListQueryRequest);

    /**
     * 从 ES 查询
     *
     * @param movieListQueryRequest
     * @return
     */
    Page<MovieList> searchFromEs(MovieListQueryRequest movieListQueryRequest);

    /**
     * 获取帖子封装
     *
     * @param movieList
     * @return
     */
    MovieListVO getMovieListVO(MovieList movieList);

    /**
     * 分页获取帖子封装
     *
     * @param movieListPage
     * @return
     */
    Page<MovieListVO> getMovieListVOPage(Page<MovieList> movieListPage);
}
