package com.yyx.movie.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yyx.movie.model.dto.movieinlist.MovieInListQueryRequest;
import com.yyx.movie.model.entity.MovieInList;
import com.yyx.movie.model.vo.MovieVO;

public interface MovieInListService extends IService<MovieInList> {
    /**
     * 获取查询条件
     *
     * @param movieInListQueryRequest
     * @return
     */
    QueryWrapper<MovieInList> getQueryWrapper(MovieInListQueryRequest movieInListQueryRequest);

    /**
     * 获取帖子封装
     *
     * @param movieInList
     * @return
     */
    MovieVO getMovieInList(MovieInList movieInList);

    /**
     * 分页获取帖子封装
     *
     * @param movieInListPage
     * @return
     */
    Page<MovieVO> getMovieInListPage(Page<MovieInList> movieInListPage);
}
