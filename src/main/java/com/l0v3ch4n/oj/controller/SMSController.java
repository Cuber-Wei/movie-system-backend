package com.l0v3ch4n.oj.controller;

import com.l0v3ch4n.oj.common.BaseResponse;
import com.l0v3ch4n.oj.common.ErrorCode;
import com.l0v3ch4n.oj.common.ResultUtils;
import com.l0v3ch4n.oj.exception.BusinessException;
import com.l0v3ch4n.oj.model.dto.sms.SMSSendRequest;
import com.l0v3ch4n.oj.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Random;

/**
 * 验证邮件接口
 */
@RestController
@RequestMapping("/sms")
@Slf4j
public class SMSController {

    @Resource
    private EmailService emailService;

    @PostMapping("/sendRegister")
    public BaseResponse<String> sendMailRegister(@RequestBody SMSSendRequest smsSendRequest) {
        if (smsSendRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String mailAddress = smsSendRequest.getUserMail();
        if (StringUtils.isBlank(mailAddress)) {
            return null;
        }
        Random random = new Random();
        String code = String.valueOf(random.nextInt(900000) + 100000);
        emailService.sendText("l0v3ch4n@qq.com", mailAddress, "OJ平台账号注册/登录验证码", "亲爱的用户，感谢您注册本平台！\n注册验证码如下：\n" + code);
        return ResultUtils.success(code);
    }

    @PostMapping("/sendForget")
    public BaseResponse<String> sendMailForget(@RequestBody SMSSendRequest smsSendRequest) {
        if (smsSendRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String mailAddress = smsSendRequest.getUserMail();
        if (StringUtils.isBlank(mailAddress)) {
            return null;
        }
        Random random = new Random();
        String code = String.valueOf(random.nextInt(900000) + 100000);
        emailService.sendText("l0v3ch4n@qq.com", mailAddress, "OJ平台账号重置密码验证码", code);
        return ResultUtils.success(code);
    }
}
