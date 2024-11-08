package com.l0v3ch4n.oj.judge.sandbox.model;

import lombok.Data;

/**
 * 判题信息
 */
@Data
public class JudgeInfo {

    /**
     * 程序执行信息
     */
    private String message;
    /**
     * 程序消耗内存
     */
    private long memory;
    /**
     * 程序执行时间
     */
    private long time;
}

