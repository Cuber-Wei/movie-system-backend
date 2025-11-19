package com.l0v3ch4n.oj.judge.sandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.l0v3ch4n.oj.common.ErrorCode;
import com.l0v3ch4n.oj.exception.BusinessException;
import com.l0v3ch4n.oj.judge.sandbox.CodeSandbox;
import com.l0v3ch4n.oj.judge.sandbox.model.ExecuteCodeRequest;
import com.l0v3ch4n.oj.judge.sandbox.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 远程代码沙箱（实际调用接口的沙箱）
 */
@Slf4j
public class RemoteCodeSandbox implements CodeSandbox {
    // 设置 请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";
    private static final String AUTH_REQUEST_SECRET = "L0v3ch4n";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String url = "http://localhost:8090/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "executeCode remoteSandbox error, message = " + responseStr);
        }
        log.info("executeCode remoteSandbox response: {}", responseStr);
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}
