package com.yyx.movie.constant;

/**
 * 审核相关常量
 */
public interface AuditConstant {
    /**
     * 待审核
     */
    Integer PENDING = 0;
    /**
     * 审核通过
     */
    Integer PASSED = 1;
    /**
     * 审核不通过
     */
    Integer FAILED = 2;
}
