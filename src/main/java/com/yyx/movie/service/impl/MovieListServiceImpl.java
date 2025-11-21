package com.yyx.movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yyx.movie.mapper.MovieListMapper;
import com.yyx.movie.model.entity.MovieList;
import com.yyx.movie.service.MovieListService;
import org.springframework.stereotype.Service;

/**
 * @author weichenghao
 * @description 针对表【movie_list(电影榜单)】的数据库操作Service实现
 * @createDate 2025-11-21 16:44:38
 */
@Service
public class MovieListServiceImpl extends ServiceImpl<MovieListMapper, MovieList>
        implements MovieListService {

}




