package com.yyx.movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyx.movie.model.entity.Movie;
import org.apache.ibatis.annotations.Param;

/**
 * @author weichenghao
 * @description 针对表【movie(电影)】的数据库操作Mapper
 * @createDate 2025-11-21 16:44:38
 * @Entity com.yyx.movie.model.entity.Movie
 */
public interface MovieMapper extends BaseMapper<Movie> {
    Long getMovieCommentNumById(@Param("movieId") Long movieId);
}




