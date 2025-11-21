package com.yyx.movie.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yyx.movie.model.entity.Collect;
import com.yyx.movie.model.entity.Movie;
import com.yyx.movie.model.entity.User;

/**
 * 帖子收藏服务
 */
public interface CollectService extends IService<Collect> {

    /**
     * 帖子收藏
     *
     * @param movieId
     * @param loginUser
     * @return
     */
    int doCollect(long movieId, User loginUser);

    /**
     * 分页获取用户收藏的帖子列表
     *
     * @param page
     * @param queryWrapper
     * @param favourUserId
     * @return
     */
    Page<Movie> listFavourMovieByPage(IPage<Movie> page, Wrapper<Movie> queryWrapper,
                                      long favourUserId);

    /**
     * 帖子收藏（内部服务）
     *
     * @param userId
     * @param movieId
     * @return
     */
    int doCollectInner(long userId, long movieId);
}
