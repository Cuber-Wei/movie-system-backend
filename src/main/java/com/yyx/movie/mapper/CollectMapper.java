package com.yyx.movie.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yyx.movie.model.entity.Collect;
import com.yyx.movie.model.entity.Movie;
import org.apache.ibatis.annotations.Param;

/**
 * @author weichenghao
 * @description 针对表【collect(电影收藏)】的数据库操作Mapper
 * @createDate 2025-11-21 16:44:38
 * @Entity com.yyx.movie.model.entity.Collect
 */
public interface CollectMapper extends BaseMapper<Collect> {
    /**
     * 分页查询收藏帖子列表
     *
     * @param page
     * @param queryWrapper
     * @param favourUserId
     * @return
     */
    Page<Movie> listFavourMovieByPage(IPage<Movie> page, @Param(Constants.WRAPPER) Wrapper<Movie> queryWrapper,
                                      long favourUserId);
}




