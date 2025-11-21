package com.yyx.movie.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yyx.movie.common.ErrorCode;
import com.yyx.movie.constant.CommonConstant;
import com.yyx.movie.exception.BusinessException;
import com.yyx.movie.exception.ThrowUtils;
import com.yyx.movie.mapper.MovieMapper;
import com.yyx.movie.model.dto.movie.MovieEsDTO;
import com.yyx.movie.model.dto.movie.MovieQueryRequest;
import com.yyx.movie.model.entity.Movie;
import com.yyx.movie.model.vo.MovieVO;
import com.yyx.movie.service.MovieService;
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
import java.util.stream.Collectors;

/**
 * 帖子服务实现
 */
@Service
@Slf4j
public class MovieServiceImpl extends ServiceImpl<MovieMapper, Movie> implements MovieService {
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public void validMovie(Movie movie, boolean add) {
        if (movie == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = movie.getTitle();
        String introduction = movie.getIntroduction();
        String tags = movie.getTag();
        String actors = movie.getActors();
        Integer duration = movie.getDuration();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, introduction, tags, actors) && duration != null, ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(introduction) && introduction.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "简介过长");
        }
        if (StringUtils.isNotBlank(actors) && introduction.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "演员信息过长");
        }
        if (duration == null || duration < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "时长设置不合法");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param movieQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Movie> getQueryWrapper(MovieQueryRequest movieQueryRequest) {
        QueryWrapper<Movie> queryWrapper = new QueryWrapper<>();
        if (movieQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = movieQueryRequest.getSearchText();
        String sortField = movieQueryRequest.getSortField();
        String sortOrder = movieQueryRequest.getSortOrder();
        Long id = movieQueryRequest.getMovieId();
        Long notId = movieQueryRequest.getNotMovieId();
        String title = movieQueryRequest.getTitle();
        String introduction = movieQueryRequest.getIntroduction();
        List<String> tagList = movieQueryRequest.getTag();
        String actors = movieQueryRequest.getActors();
        Integer duration = movieQueryRequest.getDuration();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("introduction", searchText));
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StringUtils.isNotBlank(actors), "actors", actors);
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tag", "\"" + tag + "\"");
            }
        }
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(duration), "duration", duration);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<Movie> searchFromEs(MovieQueryRequest movieQueryRequest) {
        Long id = movieQueryRequest.getMovieId();
        Long notId = movieQueryRequest.getNotMovieId();
        String searchText = movieQueryRequest.getSearchText();
        String title = movieQueryRequest.getTitle();
        String introduction = movieQueryRequest.getIntroduction();
        List<String> tagList = movieQueryRequest.getTag();
        List<String> orTagList = movieQueryRequest.getOrTag();
        // es 起始页为 0
        long current = movieQueryRequest.getCurrent() - 1;
        long pageSize = movieQueryRequest.getPageSize();
        String sortField = movieQueryRequest.getSortField();
        String sortOrder = movieQueryRequest.getSortOrder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 过滤
        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
        if (id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
        }
        if (notId != null) {
            boolQueryBuilder.mustNot(QueryBuilders.termQuery("id", notId));
        }
        // 必须包含所有标签
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("tag", tag));
            }
        }
        // 包含任何一个标签即可
        if (CollUtil.isNotEmpty(orTagList)) {
            BoolQueryBuilder orTagBoolQueryBuilder = QueryBuilders.boolQuery();
            for (String tag : orTagList) {
                orTagBoolQueryBuilder.should(QueryBuilders.termQuery("tag", tag));
            }
            orTagBoolQueryBuilder.minimumShouldMatch(1);
            boolQueryBuilder.filter(orTagBoolQueryBuilder);
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
        if (StringUtils.isNotBlank(introduction)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("introduction", introduction));
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
        SearchHits<MovieEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, MovieEsDTO.class);
        Page<Movie> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        List<Movie> resourceList = new ArrayList<>();
        page.setRecords(resourceList);
        return page;
    }

    @Override
    public MovieVO getMovieVO(Movie movie, HttpServletRequest request) {
        MovieVO movieVO = MovieVO.objToVo(movie);
        long movieId = movie.getMovieId();
        // 获取评论数
        Long commentNum = getBaseMapper().getMovieCommentNumById(movieId);
        movieVO.setCommentNum(commentNum);
        return movieVO;
    }

    @Override
    public Page<MovieVO> getMovieVOPage(Page<Movie> moviePage, HttpServletRequest request) {
        List<Movie> movieList = moviePage.getRecords();
        Page<MovieVO> movieVOPage = new Page<>(moviePage.getCurrent(), moviePage.getSize(), moviePage.getTotal());
        if (CollUtil.isEmpty(movieList)) {
            return movieVOPage;
        }
        // 填充信息
        List<MovieVO> movieVOList = movieList.stream().map(movie -> {
            MovieVO movieVO = MovieVO.objToVo(movie);
            // 获取评论数
            Long commentNum = getBaseMapper().getMovieCommentNumById(movieVO.getMovieId());
            movieVO.setCommentNum(commentNum);
            return movieVO;
        }).collect(Collectors.toList());
        movieVOPage.setRecords(movieVOList);
        return movieVOPage;
    }

}




