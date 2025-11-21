package com.l0v3ch4n.oj.service;

import com.l0v3ch4n.oj.model.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 帖子点赞服务测试
 */
@SpringBootTest
class PostThumbServiceTest {

    private static final User loginUser = new User();
    @Resource
    private PostThumbService postThumbService;

    @BeforeAll
    static void setUp() {
        loginUser.setUserId(1L);
    }

    @Test
    void doPostThumb() {
        int i = postThumbService.doPostThumb(1L, loginUser);
        Assertions.assertTrue(i >= 0);
    }
}
