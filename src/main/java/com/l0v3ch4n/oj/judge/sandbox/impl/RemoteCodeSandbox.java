package com.l0v3ch4n.oj.judge.sandbox.impl;

import com.l0v3ch4n.oj.judge.sandbox.CodeSandbox;
import com.l0v3ch4n.oj.judge.sandbox.model.ExecuteCodeRequest;
import com.l0v3ch4n.oj.judge.sandbox.model.ExecuteCodeResponse;

/**
 * 远程代码沙箱（实际调用接口的沙箱）
 */
public class RemoteCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程沙箱");
        return null;
    }
}
