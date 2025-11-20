package com.l0v3ch4n.oj.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.l0v3ch4n.oj.common.ErrorCode;
import com.l0v3ch4n.oj.constant.CommonConstant;
import com.l0v3ch4n.oj.exception.BusinessException;
import com.l0v3ch4n.oj.exception.ThrowUtils;
import com.l0v3ch4n.oj.mapper.PostCommentMapper;
import com.l0v3ch4n.oj.model.dto.postcomment.PostCommentEsDTO;
import com.l0v3ch4n.oj.model.dto.postcomment.PostCommentQueryRequest;
import com.l0v3ch4n.oj.model.entity.PostComment;
import com.l0v3ch4n.oj.model.entity.User;
import com.l0v3ch4n.oj.model.vo.PostCommentVO;
import com.l0v3ch4n.oj.model.vo.UserVO;
import com.l0v3ch4n.oj.service.PostCommentService;
import com.l0v3ch4n.oj.service.UserService;
import com.l0v3ch4n.oj.utils.SqlUtils;
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
import org.springframework.data.elasticsearch.core.SearchHit;
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
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment> implements PostCommentService {

    @Resource
    private UserService userService;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public void validPostComment(PostComment postComment, boolean add) {
        if (postComment == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String content = postComment.getContent();
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
     * @param postCommentQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<PostComment> getQueryWrapper(PostCommentQueryRequest postCommentQueryRequest) {
        QueryWrapper<PostComment> queryWrapper = new QueryWrapper<>();
        if (postCommentQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = postCommentQueryRequest.getSearchText();
        String sortField = postCommentQueryRequest.getSortField();
        String sortOrder = postCommentQueryRequest.getSortOrder();
        Long id = postCommentQueryRequest.getPostCommentId();
        String content = postCommentQueryRequest.getContent();
        Long postId = postCommentQueryRequest.getPostId();
        Long userId = postCommentQueryRequest.getUserId();
        Long notId = postCommentQueryRequest.getNotPostCommentId();
        Integer reviewStatus = postCommentQueryRequest.getReviewStatus();
        Integer notReviewStatus = postCommentQueryRequest.getNotReviewStatus();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(postId), "postId", postId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.ne(ObjectUtils.isNotEmpty(notReviewStatus), "reviewStatus", notReviewStatus);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<PostComment> searchFromEs(PostCommentQueryRequest postCommentQueryRequest) {
        Long id = postCommentQueryRequest.getPostCommentId();
        Long notId = postCommentQueryRequest.getNotPostCommentId();
        String searchText = postCommentQueryRequest.getSearchText();
        String content = postCommentQueryRequest.getContent();
        Long userId = postCommentQueryRequest.getUserId();
        // es 起始页为 0
        long current = postCommentQueryRequest.getCurrent() - 1;
        long pageSize = postCommentQueryRequest.getPageSize();
        String sortField = postCommentQueryRequest.getSortField();
        String sortOrder = postCommentQueryRequest.getSortOrder();
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
        SearchHits<PostCommentEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, PostCommentEsDTO.class);
        Page<PostComment> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        List<PostComment> resourceList = new ArrayList<>();
        // 查出结果后，从 db 获取最新动态数据（比如点赞数）
        if (searchHits.hasSearchHits()) {
            List<SearchHit<PostCommentEsDTO>> searchHitList = searchHits.getSearchHits();
            List<Long> postCommentIdList = searchHitList.stream().map(searchHit -> searchHit.getContent().getPostCommentId())
                    .collect(Collectors.toList());
            List<PostComment> postCommentList = baseMapper.selectBatchIds(postCommentIdList);
            if (postCommentList != null) {
                Map<Long, List<PostComment>> idPostCommentMap = postCommentList.stream().collect(Collectors.groupingBy(PostComment::getPostCommentId));
                postCommentIdList.forEach(postCommentId -> {
                    if (idPostCommentMap.containsKey(postCommentId)) {
                        resourceList.add(idPostCommentMap.get(postCommentId).get(0));
                    } else {
                        // 从 es 清空 db 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(postCommentId), PostCommentEsDTO.class);
                        log.info("delete postComment {}", delete);
                    }
                });
            }
        }
        page.setRecords(resourceList);
        return page;
    }

    @Override
    public PostCommentVO getPostCommentVO(PostComment postComment, HttpServletRequest request) {
        PostCommentVO postCommentVO = PostCommentVO.objToVo(postComment);
        long postCommentId = postComment.getPostCommentId();
        // 1. 关联查询用户信息
        Long userId = postComment.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        postCommentVO.setUser(userVO);
        return postCommentVO;
    }

    @Override
    public Page<PostCommentVO> getPostCommentVOPage(Page<PostComment> postCommentPage, HttpServletRequest request) {
        List<PostComment> postCommentList = postCommentPage.getRecords();
        Page<PostCommentVO> postCommentVOPage = new Page<>(postCommentPage.getCurrent(), postCommentPage.getSize(), postCommentPage.getTotal());
        if (CollUtil.isEmpty(postCommentList)) {
            return postCommentVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = postCommentList.stream().map(PostComment::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getUserId));
        // 填充信息
        List<PostCommentVO> postCommentVOList = postCommentList.stream().map(postComment -> {
            PostCommentVO postCommentVO = PostCommentVO.objToVo(postComment);
            Long userId = postComment.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            postCommentVO.setUser(userService.getUserVO(user));
            return postCommentVO;
        }).collect(Collectors.toList());
        postCommentVOPage.setRecords(postCommentVOList);
        return postCommentVOPage;
    }

}




