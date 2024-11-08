package com.l0v3ch4n.oj.model.dto.writeup;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新请求
 */
@Data
public class WriteUpUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long writeUpId;

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
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}