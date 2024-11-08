package com.l0v3ch4n.oj.model.dto.post;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 编辑请求
 */
@Data
public class PostEditRequest implements Serializable {

    /**
     * id
     */
    private Long postId;

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

    private static final long serialVersionUID = 1L;
}