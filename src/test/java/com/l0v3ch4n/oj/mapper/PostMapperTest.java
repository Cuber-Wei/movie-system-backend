package com.l0v3ch4n.oj.mapper;

import com.l0v3ch4n.oj.model.entity.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 帖子数据库操作测试
 */
@SpringBootTest
class PostMapperTest {

    @Resource
    private PostMapper postMapper;

    @Test
    void listPostWithDelete() {
        List<Post> postList = postMapper.listPostWithDelete(new Date());
        Assertions.assertNotNull(postList);
    }

    @Test
    void getCommentNumByPostId() {
        Long commentNum = postMapper.getPostCommentNumById(1991136391420989442L);
        System.out.println(commentNum);
        Assertions.assertNotNull(commentNum);
    }
}