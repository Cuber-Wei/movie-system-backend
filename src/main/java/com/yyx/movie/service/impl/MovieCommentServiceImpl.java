package com.yyx.movie.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yyx.movie.common.ErrorCode;
import com.yyx.movie.constant.CommonConstant;
import com.yyx.movie.exception.BusinessException;
import com.yyx.movie.exception.ThrowUtils;
import com.yyx.movie.mapper.MovieCommentMapper;
import com.yyx.movie.model.dto.moviecomment.MovieCommentEsDTO;
import com.yyx.movie.model.dto.moviecomment.MovieCommentQueryRequest;
import com.yyx.movie.model.entity.MovieComment;
import com.yyx.movie.model.entity.User;
import com.yyx.movie.model.vo.MovieCommentVO;
import com.yyx.movie.model.vo.UserVO;
import com.yyx.movie.service.MovieCommentService;
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
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 帖子服务实现
 */
@Service
@Slf4j
public class MovieCommentServiceImpl extends ServiceImpl<MovieCommentMapper, MovieComment> implements MovieCommentService {

    @Resource
    private UserService userService;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public void validMovieComment(MovieComment movieComment, boolean add) {
        if (movieComment == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String content = movieComment.getContent();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(content), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param movieCommentQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<MovieComment> getQueryWrapper(MovieCommentQueryRequest movieCommentQueryRequest) {
        QueryWrapper<MovieComment> queryWrapper = new QueryWrapper<>();
        if (movieCommentQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = movieCommentQueryRequest.getSearchText();
        String sortField = movieCommentQueryRequest.getSortField();
        String sortOrder = movieCommentQueryRequest.getSortOrder();
        Long id = movieCommentQueryRequest.getMovieCommentId();
        String content = movieCommentQueryRequest.getContent();
        Long movieId = movieCommentQueryRequest.getMovieId();
        Long userId = movieCommentQueryRequest.getUserId();
        Long notId = movieCommentQueryRequest.getNotMovieCommentId();
        Float score = movieCommentQueryRequest.getScore();
        Integer reviewStatus = movieCommentQueryRequest.getReviewStatus();
        Integer notReviewStatus = movieCommentQueryRequest.getNotReviewStatus();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.eq(ObjectUtils.isNotEmpty(score), "score", score);
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(movieId), "movieId", movieId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.ne(ObjectUtils.isNotEmpty(notReviewStatus), "reviewStatus", notReviewStatus);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<MovieComment> searchFromEs(MovieCommentQueryRequest movieCommentQueryRequest) {
        Long id = movieCommentQueryRequest.getMovieCommentId();
        Long notId = movieCommentQueryRequest.getNotMovieCommentId();
        String searchText = movieCommentQueryRequest.getSearchText();
        String content = movieCommentQueryRequest.getContent();
        Long userId = movieCommentQueryRequest.getUserId();
        // es 起始页为 0
        long current = movieCommentQueryRequest.getCurrent() - 1;
        long pageSize = movieCommentQueryRequest.getPageSize();
        String sortField = movieCommentQueryRequest.getSortField();
        String sortOrder = movieCommentQueryRequest.getSortOrder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 过滤
        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
        if (id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
        }
        if (notId != null) {
            boolQueryBuilder.mustNot(QueryBuilders.termQuery("id", notId));
        }
        if (userId != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("userId", userId));
        }
        // 按关键词检索
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("description", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按内容检索
        if (StringUtils.isNotBlank(content)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", content));
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
        SearchHits<MovieCommentEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, MovieCommentEsDTO.class);
        Page<MovieComment> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        List<MovieComment> resourceList = new ArrayList<>();
        page.setRecords(resourceList);
        return page;
    }

    @Override
    public MovieCommentVO getMovieCommentVO(MovieComment movieComment, HttpServletRequest request) {
        MovieCommentVO movieCommentVO = MovieCommentVO.objToVo(movieComment);
        long movieCommentId = movieComment.getMovieCommentId();
        // 1. 关联查询用户信息
        Long userId = movieComment.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        movieCommentVO.setUser(userVO);
        return movieCommentVO;
    }

    @Override
    public Page<MovieCommentVO> getMovieCommentVOPage(Page<MovieComment> movieCommentPage, HttpServletRequest request) {
        List<MovieComment> movieCommentList = movieCommentPage.getRecords();
        Page<MovieCommentVO> movieCommentVOPage = new Page<>(movieCommentPage.getCurrent(), movieCommentPage.getSize(), movieCommentPage.getTotal());
        if (CollUtil.isEmpty(movieCommentList)) {
            return movieCommentVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = movieCommentList.stream().map(MovieComment::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getUserId));
        // 填充信息
        List<MovieCommentVO> movieCommentVOList = movieCommentList.stream().map(movieComment -> {
            MovieCommentVO movieCommentVO = MovieCommentVO.objToVo(movieComment);
            Long userId = movieComment.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            movieCommentVO.setUser(userService.getUserVO(user));
            return movieCommentVO;
        }).collect(Collectors.toList());
        movieCommentVOPage.setRecords(movieCommentVOList);
        return movieCommentVOPage;
    }

}




