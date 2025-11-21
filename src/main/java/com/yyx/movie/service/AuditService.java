package com.yyx.movie.service;

import com.yyx.movie.model.enums.ReviewStatusEnum;

import javax.servlet.http.HttpServletRequest;

public interface AuditService {

    /**
     * 审核行为
     *
     * @param id        操作对象id
     * @param operation 操作结果状态
     * @param request   操作请求
     * @return
     */
    int doAudit(Long id, ReviewStatusEnum operation, HttpServletRequest request);
}
