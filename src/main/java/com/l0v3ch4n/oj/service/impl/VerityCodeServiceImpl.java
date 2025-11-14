package com.l0v3ch4n.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.l0v3ch4n.oj.common.ErrorCode;
import com.l0v3ch4n.oj.exception.BusinessException;
import com.l0v3ch4n.oj.mapper.VerityCodeMapper;
import com.l0v3ch4n.oj.model.entity.VerityCode;
import com.l0v3ch4n.oj.service.VerityCodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author weichenghao
 * @description 针对表【verity_code(验证码)】的数据库操作Service实现
 * @createDate 2025-11-14 12:19:02
 */
@Service
public class VerityCodeServiceImpl extends ServiceImpl<VerityCodeMapper, VerityCode>
        implements VerityCodeService {


    @Override
    public long createCode(String userId, String code) {
        // 校验
        if (StringUtils.isAnyBlank(userId, code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        synchronized (userId.intern()) {
            // 验证码不能重复
            QueryWrapper<VerityCode> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("code", code);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                return -1;
            }
            VerityCode verityCode = new VerityCode();
            verityCode.setUserId(userId);
            verityCode.setCode(code);
            boolean saveResult = this.save(verityCode);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return verityCode.getCodeId();
        }
    }

    @Override
    public String checkCode(String code) {
        QueryWrapper<VerityCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code);
        VerityCode verityCode = this.baseMapper.selectOne(queryWrapper);
        if (verityCode == null) {
            return null;
        } else {
            // 删除验证码，并返回用户id
            this.baseMapper.delete(queryWrapper);
            return verityCode.getUserId();
        }
    }
}




