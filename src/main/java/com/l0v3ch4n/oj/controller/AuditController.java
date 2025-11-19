package com.l0v3ch4n.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.l0v3ch4n.oj.annotation.AuthCheck;
import com.l0v3ch4n.oj.common.BaseResponse;
import com.l0v3ch4n.oj.common.ErrorCode;
import com.l0v3ch4n.oj.common.ResultUtils;
import com.l0v3ch4n.oj.constant.AuditConstant;
import com.l0v3ch4n.oj.constant.UserConstant;
import com.l0v3ch4n.oj.exception.BusinessException;
import com.l0v3ch4n.oj.model.dto.audit.AuditRequest;
import com.l0v3ch4n.oj.model.dto.post.PostQueryRequest;
import com.l0v3ch4n.oj.model.dto.postcomment.PostCommentQueryRequest;
import com.l0v3ch4n.oj.model.dto.writeup.WriteUpQueryRequest;
import com.l0v3ch4n.oj.model.entity.Post;
import com.l0v3ch4n.oj.model.entity.PostComment;
import com.l0v3ch4n.oj.model.entity.WriteUp;
import com.l0v3ch4n.oj.model.enums.AuditTypeEnum;
import com.l0v3ch4n.oj.model.enums.ReviewStatusEnum;
import com.l0v3ch4n.oj.service.AuditService;
import com.l0v3ch4n.oj.service.PostCommentService;
import com.l0v3ch4n.oj.service.PostService;
import com.l0v3ch4n.oj.service.WriteUpService;
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
    @Resource
    private PostService postService;
    @Resource
    private PostCommentService postCommentService;
    @Resource
    private WriteUpService writeUpService;

    @PostMapping("/list/post/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Post>> listUnauditedPostByPage(@RequestBody PostQueryRequest postQueryRequest) {
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        // 只查询待审核的帖子
        postQueryRequest.setReviewStatus(AuditConstant.PENDING);
        Page<Post> postPage = auditService.listPostByPage(new Page<>(current, size),
                postService.getQueryWrapper(postQueryRequest));
        return ResultUtils.success(postPage);
    }

    @PostMapping("/list/comment/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<PostComment>> listUnauditedPostCommentByPage(@RequestBody PostCommentQueryRequest commentQueryRequest) {
        long current = commentQueryRequest.getCurrent();
        long size = commentQueryRequest.getPageSize();
        // 只查询待审核的评论
        commentQueryRequest.setReviewStatus(AuditConstant.PENDING);
        Page<PostComment> commentPage = auditService.listCommentPostByPage(new Page<>(current, size),
                postCommentService.getQueryWrapper(commentQueryRequest));
        return ResultUtils.success(commentPage);
    }

    @PostMapping("/list/writeup/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<WriteUp>> listUnauditedWriteUpByPage(@RequestBody WriteUpQueryRequest writeUpQueryRequest) {
        long current = writeUpQueryRequest.getCurrent();
        long size = writeUpQueryRequest.getPageSize();
        // 只查询待审核的题解
        writeUpQueryRequest.setReviewStatus(AuditConstant.PENDING);
        Page<WriteUp> writeUpPage = auditService.listWriteUpByPage(new Page<>(current, size),
                writeUpService.getQueryWrapper(writeUpQueryRequest));
        return ResultUtils.success(writeUpPage);
    }

    @PostMapping("/do")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> doAudit(@RequestBody AuditRequest auditRequest, HttpServletRequest request) {
        if (auditRequest == null || auditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = auditService.doAudit(auditRequest.getId(), AuditTypeEnum.getEnumByValue(auditRequest.getType()),
                ReviewStatusEnum.getEnumByValue(auditRequest.getOperation()), request);
        return ResultUtils.success(result == 1);
    }
}

