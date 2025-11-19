package com.l0v3ch4n.oj.model.dto.writeup;

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
public class WriteUpQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long writeUpId;
    /**
     * id
     */
    private Long notWriteUpId;
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
     * not 审核状态
     */
    private Integer notReviewStatus;
    /**
     * 创建用户 id
     */
    private Long userId;
}