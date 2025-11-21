package com.yyx.movie.service.impl;

import com.yyx.movie.constant.AuditConstant;
import com.yyx.movie.model.entity.MovieComment;
import com.yyx.movie.model.enums.ReviewStatusEnum;
import com.yyx.movie.service.AuditService;
import com.yyx.movie.service.MovieCommentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
public class AuditServiceImpl implements AuditService {

    @Resource
    MovieCommentService movieCommentService;

    /**
     * 审核行为
     *
     * @param id        操作对象id
     * @param operation 操作结果状态
     * @param request   操作请求
     * @return
     */
    public int doAudit(Long id, ReviewStatusEnum operation, HttpServletRequest request) {
        boolean result;
        MovieComment movieComment = movieCommentService.getById(id);
        if (ReviewStatusEnum.PASSED.equals(operation)) {
            movieComment.setReviewStatus(AuditConstant.PASSED);
        } else if (ReviewStatusEnum.RETURNED.equals(operation)) {
            movieComment.setReviewStatus(AuditConstant.FAILED);
        }
        result = movieCommentService.updateById(movieComment);
        return result ? 1 : 0;
    }

}
