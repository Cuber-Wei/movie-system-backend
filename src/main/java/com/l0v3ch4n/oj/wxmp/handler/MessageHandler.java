package com.l0v3ch4n.oj.wxmp.handler;

import com.l0v3ch4n.oj.service.VerityCodeService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * 消息处理器
 **/
@Component
public class MessageHandler implements WxMpMessageHandler {

    @Resource
    private VerityCodeService verityCodeService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map,
                                    WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        String content = "我是复读机：" + wxMpXmlMessage.getContent();
        if (Objects.equals(wxMpXmlMessage.getContent(), "登录OJ")) {
            while (true) {
                Random random = new Random();
                String code = String.valueOf(random.nextInt(900000) + 100000);
                long codeId = verityCodeService.createCode(wxMpXmlMessage.getFromUser(), code);
                if (codeId != -1) {
                    return WxMpXmlOutMessage.TEXT().content("验证码：" + code)
                            .fromUser(wxMpXmlMessage.getToUser())
                            .toUser(wxMpXmlMessage.getFromUser())
                            .build();
                }
            }
        }
        return WxMpXmlOutMessage.TEXT().content(content)
                .fromUser(wxMpXmlMessage.getToUser())
                .toUser(wxMpXmlMessage.getFromUser())
                .build();
    }
}
