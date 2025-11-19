package com.l0v3ch4n.oj.model.dto.audit;

import lombok.Data;

@Data
public class AuditRequest {
    /**
     * 项目 id
     */
    private Long id;
    /**
     * 项目类型
     */
    private Integer type;
    /**
     * 审核操作
     */
    private Integer operation;
}
