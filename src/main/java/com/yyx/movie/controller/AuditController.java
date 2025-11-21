package com.yyx.movie.controller;

import com.yyx.movie.annotation.AuthCheck;
import com.yyx.movie.common.BaseResponse;
import com.yyx.movie.common.ErrorCode;
import com.yyx.movie.common.ResultUtils;
import com.yyx.movie.constant.UserConstant;
import com.yyx.movie.exception.BusinessException;
import com.yyx.movie.model.dto.audit.AuditRequest;
import com.yyx.movie.model.enums.ReviewStatusEnum;
import com.yyx.movie.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 审核接口
 */
@RestController
@RequestMapping("/audit")
@Slf4j
public class AuditController {
    @Resource
    private AuditService auditService;

    @PostMapping("/do")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> doAudit(@RequestBody AuditRequest auditRequest, HttpServletRequest request) {
        if (auditRequest == null || auditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = auditService.doAudit(auditRequest.getId(), ReviewStatusEnum.getEnumByValue(auditRequest.getOperation()), request);
        return ResultUtils.success(result == 1);
    }
}

