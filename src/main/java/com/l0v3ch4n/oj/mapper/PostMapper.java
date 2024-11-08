package com.l0v3ch4n.oj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.l0v3ch4n.oj.model.entity.Post;

import java.util.Date;
import java.util.List;

/**
 * @author Lovechan
 * @description 针对表【post(帖子)】的数据库操作Mapper
 * @createDate 2024-11-09 16:56:47
 * @Entity com.l0v3ch4n.oj.model.entity.Post
 */
public interface PostMapper extends BaseMapper<Post> {

    List<Post> listPostWithDelete(Date minUpdateTime);
}




