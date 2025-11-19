package com.l0v3ch4n.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.l0v3ch4n.oj.annotation.AuthCheck;
import com.l0v3ch4n.oj.common.BaseResponse;
import com.l0v3ch4n.oj.common.DeleteRequest;
import com.l0v3ch4n.oj.common.ErrorCode;
import com.l0v3ch4n.oj.common.ResultUtils;
import com.l0v3ch4n.oj.constant.AuditConstant;
import com.l0v3ch4n.oj.constant.UserConstant;
import com.l0v3ch4n.oj.exception.BusinessException;
import com.l0v3ch4n.oj.exception.ThrowUtils;
import com.l0v3ch4n.oj.model.dto.postcomment.PostCommentAddRequest;
import com.l0v3ch4n.oj.model.dto.postcomment.PostCommentQueryRequest;
import com.l0v3ch4n.oj.model.entity.PostComment;
import com.l0v3ch4n.oj.model.entity.User;
import com.l0v3ch4n.oj.model.vo.PostCommentVO;
import com.l0v3ch4n.oj.service.PostCommentService;
import com.l0v3ch4n.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 题解接口
 */
@RestController
@RequestMapping("/postComment")
@Slf4j
public class PostCommentController {

    @Resource
    private PostCommentService postCommentService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param postCommentAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPostComment(@RequestBody PostCommentAddRequest postCommentAddRequest, HttpServletRequest request) {
        if (postCommentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PostComment postComment = new PostComment();
        BeanUtils.copyProperties(postCommentAddRequest, postComment);
        postCommentService.validPostComment(postComment, true);
        User loginUser = userService.getLoginUser(request);
        postComment.setUserId(loginUser.getUserId());
        postComment.setReviewStatus(AuditConstant.PENDING);
        boolean result = postCommentService.save(postComment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newPostCommentId = postComment.getPostCommentId();
        return ResultUtils.success(newPostCommentId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePostComment(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        PostComment oldPostComment = postCommentService.getById(id);
        ThrowUtils.throwIf(oldPostComment == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldPostComment.getUserId().equals(user.getUserId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = postCommentService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<PostCommentVO> getPostCommentVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PostComment postComment = postCommentService.getById(id);
        if (postComment == null || !Objects.equals(postComment.getReviewStatus(), AuditConstant.PASSED)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(postCommentService.getPostCommentVO(postComment, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param postCommentQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<PostComment>> listPostCommentByPage(@RequestBody PostCommentQueryRequest postCommentQueryRequest) {
        long current = postCommentQueryRequest.getCurrent();
        long size = postCommentQueryRequest.getPageSize();
        Page<PostComment> postCommentPage = postCommentService.page(new Page<>(current, size),
                postCommentService.getQueryWrapper(postCommentQueryRequest));
        return ResultUtils.success(postCommentPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param postCommentQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PostCommentVO>> listPostCommentVOByPage(@RequestBody PostCommentQueryRequest postCommentQueryRequest,
                                                                     HttpServletRequest request) {
        long current = postCommentQueryRequest.getCurrent();
        long size = postCommentQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<PostComment> postCommentPage = postCommentService.page(new Page<>(current, size),
                postCommentService.getQueryWrapper(postCommentQueryRequest));
        return ResultUtils.success(postCommentService.getPostCommentVOPage(postCommentPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param postCommentQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<PostCommentVO>> listMyPostCommentVOByPage(@RequestBody PostCommentQueryRequest postCommentQueryRequest,
                                                                       HttpServletRequest request) {
        if (postCommentQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        postCommentQueryRequest.setUserId(loginUser.getUserId());
        long current = postCommentQueryRequest.getCurrent();
        long size = postCommentQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<PostComment> postCommentPage = postCommentService.page(new Page<>(current, size),
                postCommentService.getQueryWrapper(postCommentQueryRequest));
        return ResultUtils.success(postCommentService.getPostCommentVOPage(postCommentPage, request));
    }

    // endregion

    /**
     * 分页搜索（从 ES 查询，封装类）
     *
     * @param postCommentQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/search/page/vo")
    public BaseResponse<Page<PostCommentVO>> searchPostCommentVOByPage(@RequestBody PostCommentQueryRequest postCommentQueryRequest,
                                                                       HttpServletRequest request) {
        long size = postCommentQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<PostComment> postCommentPage = postCommentService.searchFromEs(postCommentQueryRequest);
        return ResultUtils.success(postCommentService.getPostCommentVOPage(postCommentPage, request));
    }

}
