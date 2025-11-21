package com.yyx.movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyx.movie.model.entity.MovieList;
import org.apache.ibatis.annotations.Param;

/**
 * @author weichenghao
 * @description 针对表【movie_list(电影榜单)】的数据库操作Mapper
 * @createDate 2025-11-21 16:44:38
 * @Entity com.yyx.movie.model.entity.MovieList
 */
public interface MovieListMapper extends BaseMapper<MovieList> {
    Long getMovieNumById(@Param("movieListId") Long movieListId);
}




