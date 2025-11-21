package com.yyx.movie.service;

public interface EmailService {
    /**
     * 文本文件发送
     *
     * @param from
     * @param to
     * @param subject
     * @param content
     */
    void sendText(String from, String to, String subject, String content);

}
