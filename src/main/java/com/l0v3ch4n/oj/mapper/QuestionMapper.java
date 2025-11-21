package com.l0v3ch4n.oj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.l0v3ch4n.oj.model.entity.Question;
import org.apache.ibatis.annotations.Param;

/**
 * @author Lovechan
 * @description 针对表【question(题目)】的数据库操作Mapper
 * @createDate 2024-11-09 16:56:47
 * @Entity com.l0v3ch4n.oj.model.entity.Question
 */
public interface QuestionMapper extends BaseMapper<Question> {

    Long getCommitNumById(@Param("questionId") Long questionId);

    Long getAcceptedNumById(@Param("questionId") Long questionId);

}




