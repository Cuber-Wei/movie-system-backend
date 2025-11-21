package com.yyx.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yyx.movie.common.ErrorCode;
import com.yyx.movie.exception.BusinessException;
import com.yyx.movie.mapper.CollectMapper;
import com.yyx.movie.model.entity.Collect;
import com.yyx.movie.model.entity.Movie;
import com.yyx.movie.model.entity.User;
import com.yyx.movie.service.CollectService;
import com.yyx.movie.service.MovieService;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 帖子收藏服务实现
 */
@Service
public class CollectServiceImpl extends ServiceImpl<CollectMapper, Collect>
        implements CollectService {

    @Resource
    private MovieService movieService;

    /**
     * 帖子收藏
     *
     * @param movieId
     * @param loginUser
     * @return
     */
    @Override
    public int doCollect(long movieId, User loginUser) {
        // 判断是否存在
        Movie movie = movieService.getById(movieId);
        if (movie == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已帖子收藏
        long userId = loginUser.getUserId();
        // 每个用户串行帖子收藏
        // 锁必须要包裹住事务方法
        CollectService movieFavourService = (CollectService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return movieFavourService.doCollectInner(userId, movieId);
        }
    }

    @Override
    public Page<Movie> listFavourMovieByPage(IPage<Movie> page, Wrapper<Movie> queryWrapper, long favourUserId) {
        if (favourUserId <= 0) {
            return new Page<>();
        }
        return baseMapper.listFavourMovieByPage(page, queryWrapper, favourUserId);
    }

    /**
     * 封装了事务的方法
     *
     * @param userId
     * @param movieId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doCollectInner(long userId, long movieId) {
        Collect movieFavour = new Collect();
        movieFavour.setUserId(userId);
        movieFavour.setMovieId(movieId);
        QueryWrapper<Collect> movieFavourQueryWrapper = new QueryWrapper<>(movieFavour);
        Collect oldCollect = this.getOne(movieFavourQueryWrapper);
        boolean result;
        // 已收藏
        if (oldCollect != null) {
            result = this.remove(movieFavourQueryWrapper);
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            // 未帖子收藏
            result = this.save(movieFavour);
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
        return 1;
    }

}




