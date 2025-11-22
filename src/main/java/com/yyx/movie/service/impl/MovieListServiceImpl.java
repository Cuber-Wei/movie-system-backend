package com.yyx.movie.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yyx.movie.common.ErrorCode;
import com.yyx.movie.constant.CommonConstant;
import com.yyx.movie.exception.BusinessException;
import com.yyx.movie.exception.ThrowUtils;
import com.yyx.movie.mapper.MovieListMapper;
import com.yyx.movie.model.dto.movielist.MovieListEsDTO;
import com.yyx.movie.model.dto.movielist.MovieListQueryRequest;
import com.yyx.movie.model.entity.MovieInList;
import com.yyx.movie.model.entity.MovieList;
import com.yyx.movie.model.entity.User;
import com.yyx.movie.model.vo.MovieListVO;
import com.yyx.movie.model.vo.UserVO;
import com.yyx.movie.service.MovieInListService;
import com.yyx.movie.service.MovieListService;
import com.yyx.movie.service.UserService;
import com.yyx.movie.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 帖子服务实现
 */
@Service
@Slf4j
public class MovieListServiceImpl extends ServiceImpl<MovieListMapper, MovieList> implements MovieListService {
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Resource
    private MovieInListService movieInListService;
    @Resource
    private UserService userService;

    @Override
    public void validMovieList(MovieList movieList, boolean add) {
        if (movieList == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = movieList.getTitle();
        String listIntroduction = movieList.getListIntroduction();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, listIntroduction), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(listIntroduction) && listIntroduction.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "简介过长");
        }
    }

    /**
     * 添加电影到榜单
     *
     * @param movieListId
     * @param movieId
     * @return
     */
    @Override
    public MovieListVO addMovieToList(Long movieListId, Long movieId) {
        if (movieListId == null || movieId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        MovieInList movieInList = new MovieInList();
        movieInList.setMovieId(movieId);
        movieInList.setMovieListId(movieListId);
        boolean res = movieInListService.save(movieInList);
        if (!res) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        MovieList movieList = this.getById(movieListId);
        return this.getMovieListVO(movieList);
    }

    /**
     * 获取查询包装类
     *
     * @param movieListQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<MovieList> getQueryWrapper(MovieListQueryRequest movieListQueryRequest) {
        QueryWrapper<MovieList> queryWrapper = new QueryWrapper<>();
        if (movieListQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = movieListQueryRequest.getSearchText();
        String sortField = movieListQueryRequest.getSortField();
        String sortOrder = movieListQueryRequest.getSortOrder();
        Long id = movieListQueryRequest.getMovieListId();
        Long notId = movieListQueryRequest.getNotMovieListId();
        String title = movieListQueryRequest.getTitle();
        String listIntroduction = movieListQueryRequest.getListIntroduction();
        Long userId = movieListQueryRequest.getUserId();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("introduction", searchText));
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(listIntroduction), "listIntroduction", listIntroduction);
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<MovieList> searchFromEs(MovieListQueryRequest movieListQueryRequest) {
        Long id = movieListQueryRequest.getMovieListId();
        Long notId = movieListQueryRequest.getNotMovieListId();
        String searchText = movieListQueryRequest.getSearchText();
        String title = movieListQueryRequest.getTitle();
        String listIntroduction = movieListQueryRequest.getListIntroduction();
        // es 起始页为 0
        long current = movieListQueryRequest.getCurrent() - 1;
        long pageSize = movieListQueryRequest.getPageSize();
        String sortField = movieListQueryRequest.getSortField();
        String sortOrder = movieListQueryRequest.getSortOrder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 过滤
        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
        if (id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
        }
        if (notId != null) {
            boolQueryBuilder.mustNot(QueryBuilders.termQuery("id", notId));
        }
        // 按关键词检索
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("description", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("introduction", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按标题检索
        if (StringUtils.isNotBlank(title)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", title));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按内容检索
        if (StringUtils.isNotBlank(listIntroduction)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("introduction", listIntroduction));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 排序
        SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
        if (StringUtils.isNotBlank(sortField)) {
            sortBuilder = SortBuilders.fieldSort(sortField);
            sortBuilder.order(CommonConstant.SORT_ORDER_ASC.equals(sortOrder) ? SortOrder.ASC : SortOrder.DESC);
        }
        // 分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);
        // 构造查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageRequest).withSorts(sortBuilder).build();
        SearchHits<MovieListEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, MovieListEsDTO.class);
        Page<MovieList> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        List<MovieList> resourceList = new ArrayList<>();
        page.setRecords(resourceList);
        return page;
    }

    @Override
    public MovieListVO getMovieListVO(MovieList movieList) {
        MovieListVO movieListVO = MovieListVO.objToVo(movieList);
        long movieListId = movieList.getMovieListId();
        // 1. 关联查询用户信息
        Long userId = movieList.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        movieListVO.setUser(userVO);
        // 获取电影数
        Long movieNum = getBaseMapper().getMovieNumById(movieListId);
        movieListVO.setMovieNum(movieNum);
        return movieListVO;
    }

    @Override
    public Page<MovieListVO> getMovieListVOPage(Page<MovieList> movieListPage) {
        List<MovieList> movieList = movieListPage.getRecords();
        Page<MovieListVO> movieListVOPage = new Page<>(movieListPage.getCurrent(), movieListPage.getSize(), movieListPage.getTotal());
        if (CollUtil.isEmpty(movieList)) {
            return movieListVOPage;
        }
        // 填充信息
        List<MovieListVO> movieListVOList = movieList.stream().map(movielist -> {
            MovieListVO movieListVO = MovieListVO.objToVo(movielist);
            // 获取电影数
            Long commentNum = getBaseMapper().getMovieNumById(movieListVO.getMovieListId());
            movieListVO.setMovieNum(commentNum);
            // 1. 关联查询用户信息
            Long userId = movielist.getUserId();
            User user = null;
            if (userId != null && userId > 0) {
                user = userService.getById(userId);
            }
            UserVO userVO = userService.getUserVO(user);
            movieListVO.setUser(userVO);
            return movieListVO;
        }).collect(Collectors.toList());
        movieListVOPage.setRecords(movieListVOList);
        return movieListVOPage;
    }

}




