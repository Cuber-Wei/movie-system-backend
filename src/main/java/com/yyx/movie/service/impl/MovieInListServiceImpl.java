package com.yyx.movie.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yyx.movie.constant.CommonConstant;
import com.yyx.movie.mapper.MovieInListMapper;
import com.yyx.movie.model.dto.movieinlist.MovieInListQueryRequest;
import com.yyx.movie.model.entity.MovieInList;
import com.yyx.movie.model.vo.MovieVO;
import com.yyx.movie.service.MovieInListService;
import com.yyx.movie.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 帖子服务实现
 */
@Service
@Slf4j
public class MovieInListServiceImpl extends ServiceImpl<MovieInListMapper, MovieInList> implements MovieInListService {

    /**
     * 获取查询包装类
     *
     * @param movieInListQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<MovieInList> getQueryWrapper(MovieInListQueryRequest movieInListQueryRequest) {
        QueryWrapper<MovieInList> queryWrapper = new QueryWrapper<>();
        if (movieInListQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = movieInListQueryRequest.getSearchText();
        String sortField = movieInListQueryRequest.getSortField();
        String sortOrder = movieInListQueryRequest.getSortOrder();
        Long id = movieInListQueryRequest.getMovieInListId();
        Long notId = movieInListQueryRequest.getNotMovieInListId();
        Long movieId = movieInListQueryRequest.getMovieId();
        Long movieListId = movieInListQueryRequest.getMovieListId();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("introduction", searchText));
        }
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(movieId), "movieId", movieId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(movieListId), "movieListId", movieListId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取帖子封装
     *
     * @param movieInList
     * @return
     */
    @Override
    public MovieVO getMovieInList(MovieInList movieInList) {
        Long movieId = movieInList.getMovieId();
        MovieVO movieVO = new MovieVO();
        movieVO.setMovieId(movieId);
        return movieVO;
    }

    /**
     * 分页获取帖子封装
     *
     * @param movieInListPage
     * @return
     */
    @Override
    public Page<MovieVO> getMovieInListPage(Page<MovieInList> movieInListPage) {
        List<MovieInList> movieInList = movieInListPage.getRecords();
        Page<MovieVO> movieVOPage = new Page<>(movieInListPage.getCurrent(), movieInListPage.getSize(), movieInListPage.getTotal());
        if (CollUtil.isEmpty(movieInList)) {
            return movieVOPage;
        }
        // 填充信息
        List<MovieVO> movieListVOList = movieInList.stream().map(movieinlist -> {
            MovieVO movieListVO = this.getMovieInList(movieinlist);
            return movieListVO;
        }).collect(Collectors.toList());
        movieVOPage.setRecords(movieListVOList);
        return movieVOPage;
    }

}




