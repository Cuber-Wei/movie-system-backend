package com.l0v3ch4n.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.l0v3ch4n.oj.model.entity.PostThumb;
import com.l0v3ch4n.oj.model.entity.User;

/**
 * 帖子点赞服务
 */
public interface PostThumbService extends IService<PostThumb> {

    /**
     * 点赞
     *
     * @param postId
     * @param loginUser
     * @return
     */
    int doPostThumb(long postId, User loginUser);

    /**
     * 帖子点赞（内部服务）
     *
     * @param userId
     * @param postId
     * @return
     */
    int doPostThumbInner(long userId, long postId);
}
