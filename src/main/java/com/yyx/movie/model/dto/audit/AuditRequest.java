package com.yyx.movie.model.dto.audit;

import lombok.Data;

@Data
public class AuditRequest {
    /**
     * 项目 id
     */
    private Long id;
    /**
     * 审核操作
     */
    private Integer operation;
}
