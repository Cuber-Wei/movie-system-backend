package com.l0v3ch4n.oj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.l0v3ch4n.oj.model.entity.Post;
import com.l0v3ch4n.oj.model.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 帖子收藏服务测试
 */
@SpringBootTest
class PostFavourServiceTest {

    private static final User loginUser = new User();
    @Resource
    private PostFavourService postFavourService;

    @BeforeAll
    static void setUp() {
        loginUser.setUserId(1L);
    }

    @Test
    void doPostFavour() {
        int i = postFavourService.doPostFavour(1L, loginUser);
        Assertions.assertTrue(i >= 0);
    }

    @Test
    void listFavourPostByPage() {
        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
        postQueryWrapper.eq("id", 1L);
        postFavourService.listFavourPostByPage(Page.of(0, 1), postQueryWrapper, loginUser.getUserId());
    }
}
