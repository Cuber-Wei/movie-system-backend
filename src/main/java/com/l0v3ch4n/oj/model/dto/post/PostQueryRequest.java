package com.l0v3ch4n.oj.model.dto.post;

import com.l0v3ch4n.oj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long postId;

    /**
     * id
     */
    private Long notPostId;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tag;

    /**
     * 至少有一个标签
     */
    private List<String> orTag;

    /**
     * 审核状态
     */
    private Integer reviewStatus;

    /**
     * 创建用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}