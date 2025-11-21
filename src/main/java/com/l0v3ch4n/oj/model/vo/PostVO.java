package com.l0v3ch4n.oj.model.vo;

import cn.hutool.json.JSONUtil;
import com.l0v3ch4n.oj.model.entity.Post;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子视图
 */
@Data
public class PostVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long postId;
    /**
     * 创建用户 id
     */
    private Long userId;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 标签列表 json
     */
    private List<String> tag;
    /**
     * 审核状态
     */
    private Integer reviewStatus;
    /**
     * 评论数
     */
    private Long commentNum;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 创建人信息
     */
    private UserVO user;

    /**
     * 包装类转对象
     *
     * @param postVO
     * @return
     */
    public static Post voToObj(PostVO postVO) {
        if (postVO == null) {
            return null;
        }
        Post post = new Post();
        BeanUtils.copyProperties(postVO, post);
        List<String> tagList = postVO.getTag();
        post.setTag(JSONUtil.toJsonStr(tagList));
        return post;
    }

    /**
     * 对象转包装类
     *
     * @param post
     * @return
     */
    public static PostVO objToVo(Post post) {
        if (post == null) {
            return null;
        }
        PostVO postVO = new PostVO();
        BeanUtils.copyProperties(post, postVO);
        postVO.setTag(JSONUtil.toList(post.getTag(), String.class));
        return postVO;
    }
}
