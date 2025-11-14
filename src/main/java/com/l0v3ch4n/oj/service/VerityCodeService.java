package com.l0v3ch4n.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.l0v3ch4n.oj.model.entity.VerityCode;

/**
 * @author weichenghao
 * @description 针对表【verity_code(验证码)】的数据库操作Service
 * @createDate 2025-11-14 12:19:02
 */
public interface VerityCodeService extends IService<VerityCode> {
    /**
     * 创建开放平台验证码
     *
     * @param userId 用户开放平台id
     * @param code   验证码内容
     * @return 验证码 id
     */
    long createCode(String userId, String code);

    /**
     * 校验验证码是否正确
     *
     * @param code 验证码内容
     * @return null 或 用户公众id
     */
    String checkCode(String code);

}
