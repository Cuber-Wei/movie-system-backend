package com.l0v3ch4n.oj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.l0v3ch4n.oj.model.dto.writeup.WriteUpQueryRequest;
import com.l0v3ch4n.oj.model.entity.WriteUp;
import com.l0v3ch4n.oj.model.vo.WriteUpVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 题解服务
 */
public interface WriteUpService extends IService<WriteUp> {

    /**
     * 校验
     *
     * @param writeUp
     * @param add
     */
    void validWriteUp(WriteUp writeUp, boolean add);

    /**
     * 获取查询条件
     *
     * @param writeUpQueryRequest
     * @return
     */
    QueryWrapper<WriteUp> getQueryWrapper(WriteUpQueryRequest writeUpQueryRequest);

    /**
     * 从 ES 查询
     *
     * @param writeUpQueryRequest
     * @return
     */
    Page<WriteUp> searchFromEs(WriteUpQueryRequest writeUpQueryRequest);

    /**
     * 获取题解封装
     *
     * @param writeUp
     * @param request
     * @return
     */
    WriteUpVO getWriteUpVO(WriteUp writeUp, HttpServletRequest request);

    /**
     * 分页获取题解封装
     *
     * @param writeUpPage
     * @param request
     * @return
     */
    Page<WriteUpVO> getWriteUpVOPage(Page<WriteUp> writeUpPage, HttpServletRequest request);
}
