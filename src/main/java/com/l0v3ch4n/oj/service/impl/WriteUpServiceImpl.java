package com.l0v3ch4n.oj.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.l0v3ch4n.oj.common.ErrorCode;
import com.l0v3ch4n.oj.constant.CommonConstant;
import com.l0v3ch4n.oj.exception.BusinessException;
import com.l0v3ch4n.oj.exception.ThrowUtils;
import com.l0v3ch4n.oj.mapper.WriteUpMapper;
import com.l0v3ch4n.oj.model.dto.writeup.WriteUpEsDTO;
import com.l0v3ch4n.oj.model.dto.writeup.WriteUpQueryRequest;
import com.l0v3ch4n.oj.model.entity.User;
import com.l0v3ch4n.oj.model.entity.WriteUp;
import com.l0v3ch4n.oj.model.vo.UserVO;
import com.l0v3ch4n.oj.model.vo.WriteUpVO;
import com.l0v3ch4n.oj.service.UserService;
import com.l0v3ch4n.oj.service.WriteUpService;
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
public class WriteUpServiceImpl extends ServiceImpl<WriteUpMapper, WriteUp> implements WriteUpService {

    @Resource
    private UserService userService;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public void validWriteUp(WriteUp writeUp, boolean add) {
        if (writeUp == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = writeUp.getTitle();
        String content = writeUp.getContent();
        String tags = writeUp.getTag();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param writeUpQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<WriteUp> getQueryWrapper(WriteUpQueryRequest writeUpQueryRequest) {
        QueryWrapper<WriteUp> queryWrapper = new QueryWrapper<>();
        if (writeUpQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = writeUpQueryRequest.getSearchText();
        String sortField = writeUpQueryRequest.getSortField();
        String sortOrder = writeUpQueryRequest.getSortOrder();
        Long id = writeUpQueryRequest.getWriteUpId();
        String title = writeUpQueryRequest.getTitle();
        String content = writeUpQueryRequest.getContent();
        List<String> tagList = writeUpQueryRequest.getTag();
        Long userId = writeUpQueryRequest.getUserId();
        Long notId = writeUpQueryRequest.getNotWriteUpId();
        Integer reviewStatus = writeUpQueryRequest.getReviewStatus();
        Integer notReviewStatus = writeUpQueryRequest.getNotReviewStatus();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tag", "\"" + tag + "\"");
            }
        }
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.ne(ObjectUtils.isNotEmpty(notReviewStatus), "notReviewStatus", notReviewStatus);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<WriteUp> searchFromEs(WriteUpQueryRequest writeUpQueryRequest) {
        Long id = writeUpQueryRequest.getWriteUpId();
        Long notId = writeUpQueryRequest.getNotWriteUpId();
        String searchText = writeUpQueryRequest.getSearchText();
        String title = writeUpQueryRequest.getTitle();
        String content = writeUpQueryRequest.getContent();
        List<String> tagList = writeUpQueryRequest.getTag();
        List<String> orTagList = writeUpQueryRequest.getOrTag();
        Long userId = writeUpQueryRequest.getUserId();
        // es 起始页为 0
        long current = writeUpQueryRequest.getCurrent() - 1;
        long pageSize = writeUpQueryRequest.getPageSize();
        String sortField = writeUpQueryRequest.getSortField();
        String sortOrder = writeUpQueryRequest.getSortOrder();
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
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按标题检索
        if (StringUtils.isNotBlank(title)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", title));
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
        SearchHits<WriteUpEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, WriteUpEsDTO.class);
        Page<WriteUp> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        List<WriteUp> resourceList = new ArrayList<>();
        // 查出结果后，从 db 获取最新动态数据（比如点赞数）
        if (searchHits.hasSearchHits()) {
            List<SearchHit<WriteUpEsDTO>> searchHitList = searchHits.getSearchHits();
            List<Long> writeUpIdList = searchHitList.stream().map(searchHit -> searchHit.getContent().getWriteUpId())
                    .collect(Collectors.toList());
            List<WriteUp> writeUpList = baseMapper.selectBatchIds(writeUpIdList);
            if (writeUpList != null) {
                Map<Long, List<WriteUp>> idWriteUpMap = writeUpList.stream().collect(Collectors.groupingBy(WriteUp::getWriteUpId));
                writeUpIdList.forEach(writeUpId -> {
                    if (idWriteUpMap.containsKey(writeUpId)) {
                        resourceList.add(idWriteUpMap.get(writeUpId).get(0));
                    } else {
                        // 从 es 清空 db 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(writeUpId), WriteUpEsDTO.class);
                        log.info("delete writeUp {}", delete);
                    }
                });
            }
        }
        page.setRecords(resourceList);
        return page;
    }

    @Override
    public WriteUpVO getWriteUpVO(WriteUp writeUp, HttpServletRequest request) {
        WriteUpVO writeUpVO = WriteUpVO.objToVo(writeUp);
        long writeUpId = writeUp.getWriteUpId();
        // 1. 关联查询用户信息
        Long userId = writeUp.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        writeUpVO.setUser(userVO);
        return writeUpVO;
    }

    @Override
    public Page<WriteUpVO> getWriteUpVOPage(Page<WriteUp> writeUpPage, HttpServletRequest request) {
        List<WriteUp> writeUpList = writeUpPage.getRecords();
        Page<WriteUpVO> writeUpVOPage = new Page<>(writeUpPage.getCurrent(), writeUpPage.getSize(), writeUpPage.getTotal());
        if (CollUtil.isEmpty(writeUpList)) {
            return writeUpVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = writeUpList.stream().map(WriteUp::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getUserId));
        // 填充信息
        List<WriteUpVO> writeUpVOList = writeUpList.stream().map(writeUp -> {
            WriteUpVO writeUpVO = WriteUpVO.objToVo(writeUp);
            Long userId = writeUp.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            writeUpVO.setUser(userService.getUserVO(user));
            return writeUpVO;
        }).collect(Collectors.toList());
        writeUpVOPage.setRecords(writeUpVOList);
        return writeUpVOPage;
    }

}




