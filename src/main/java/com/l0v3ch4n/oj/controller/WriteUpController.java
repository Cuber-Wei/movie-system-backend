package com.l0v3ch4n.oj.controller;

import cn.hutool.json.JSONUtil;
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
import com.l0v3ch4n.oj.model.dto.writeup.WriteUpAddRequest;
import com.l0v3ch4n.oj.model.dto.writeup.WriteUpQueryRequest;
import com.l0v3ch4n.oj.model.dto.writeup.WriteUpUpdateRequest;
import com.l0v3ch4n.oj.model.entity.User;
import com.l0v3ch4n.oj.model.entity.WriteUp;
import com.l0v3ch4n.oj.model.vo.WriteUpVO;
import com.l0v3ch4n.oj.service.UserService;
import com.l0v3ch4n.oj.service.WriteUpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题解接口
 */
@RestController
@RequestMapping("/writeUp")
@Slf4j
public class WriteUpController {

    @Resource
    private WriteUpService writeUpService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param writeUpAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addWriteUp(@RequestBody WriteUpAddRequest writeUpAddRequest, HttpServletRequest request) {
        if (writeUpAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        WriteUp writeUp = new WriteUp();
        BeanUtils.copyProperties(writeUpAddRequest, writeUp);
        List<String> tags = writeUpAddRequest.getTag();
        if (tags != null) {
            writeUp.setTag(JSONUtil.toJsonStr(tags));
        }
        writeUpService.validWriteUp(writeUp, true);
        User loginUser = userService.getLoginUser(request);
        writeUp.setUserId(loginUser.getUserId());
        writeUp.setReviewStatus(AuditConstant.PENDING);
        boolean result = writeUpService.save(writeUp);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newWriteUpId = writeUp.getWriteUpId();
        return ResultUtils.success(newWriteUpId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteWriteUp(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        WriteUp oldWriteUp = writeUpService.getById(id);
        ThrowUtils.throwIf(oldWriteUp == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldWriteUp.getUserId().equals(user.getUserId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = writeUpService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param writeUpUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateWriteUp(@RequestBody WriteUpUpdateRequest writeUpUpdateRequest) {
        if (writeUpUpdateRequest == null || writeUpUpdateRequest.getWriteUpId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        WriteUp writeUp = new WriteUp();
        BeanUtils.copyProperties(writeUpUpdateRequest, writeUp);
        List<String> tags = writeUpUpdateRequest.getTags();
        if (tags != null) {
            writeUp.setTag(JSONUtil.toJsonStr(tags));
        }
        // 参数校验
        writeUpService.validWriteUp(writeUp, false);
        long id = writeUpUpdateRequest.getWriteUpId();
        // 判断是否存在
        WriteUp oldWriteUp = writeUpService.getById(id);
        ThrowUtils.throwIf(oldWriteUp == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = writeUpService.updateById(writeUp);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<WriteUpVO> getWriteUpVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        WriteUp writeUp = writeUpService.getById(id);
        if (writeUp == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(writeUpService.getWriteUpVO(writeUp, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param writeUpQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<WriteUp>> listWriteUpByPage(@RequestBody WriteUpQueryRequest writeUpQueryRequest) {
        long current = writeUpQueryRequest.getCurrent();
        long size = writeUpQueryRequest.getPageSize();
        Page<WriteUp> writeUpPage = writeUpService.page(new Page<>(current, size),
                writeUpService.getQueryWrapper(writeUpQueryRequest));
        return ResultUtils.success(writeUpPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param writeUpQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<WriteUpVO>> listWriteUpVOByPage(@RequestBody WriteUpQueryRequest writeUpQueryRequest,
                                                             HttpServletRequest request) {
        long current = writeUpQueryRequest.getCurrent();
        long size = writeUpQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<WriteUp> writeUpPage = writeUpService.page(new Page<>(current, size),
                writeUpService.getQueryWrapper(writeUpQueryRequest));
        return ResultUtils.success(writeUpService.getWriteUpVOPage(writeUpPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param writeUpQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<WriteUpVO>> listMyWriteUpVOByPage(@RequestBody WriteUpQueryRequest writeUpQueryRequest,
                                                               HttpServletRequest request) {
        if (writeUpQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        writeUpQueryRequest.setUserId(loginUser.getUserId());
        long current = writeUpQueryRequest.getCurrent();
        long size = writeUpQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<WriteUp> writeUpPage = writeUpService.page(new Page<>(current, size),
                writeUpService.getQueryWrapper(writeUpQueryRequest));
        return ResultUtils.success(writeUpService.getWriteUpVOPage(writeUpPage, request));
    }

    // endregion

    /**
     * 分页搜索（从 ES 查询，封装类）
     *
     * @param writeUpQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/search/page/vo")
    public BaseResponse<Page<WriteUpVO>> searchWriteUpVOByPage(@RequestBody WriteUpQueryRequest writeUpQueryRequest,
                                                               HttpServletRequest request) {
        long size = writeUpQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<WriteUp> writeUpPage = writeUpService.searchFromEs(writeUpQueryRequest);
        return ResultUtils.success(writeUpService.getWriteUpVOPage(writeUpPage, request));
    }

}
