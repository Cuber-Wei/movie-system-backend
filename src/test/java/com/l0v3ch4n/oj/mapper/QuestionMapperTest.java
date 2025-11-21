package com.l0v3ch4n.oj.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class QuestionMapperTest {
    @Resource
    private QuestionMapper questionMapper;

    @Test
    void listPostWithDelete() {
        Long commitNum = questionMapper.getCommitNumById(1989607709917335554L);
        System.out.println(commitNum);
        Assertions.assertNotNull(commitNum);
    }
}
