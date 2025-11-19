package com.l0v3ch4n.oj.model.dto.postcomment;

import com.l0v3ch4n.oj.model.entity.PostComment;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题解 ES 包装类
 **/
// todo 取消注释开启 ES（须先配置 ES）
//@Document(indexName = "postComment")
@Data
public class PostCommentEsDTO implements Serializable {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @Id
    private Long postCommentId;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 标签列表
     */
    private List<String> tag;
    /**
     * 创建用户 id
     */
    private Long userId;
    /**
     * 创建时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date createTime;
    /**
     * 更新时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date updateTime;
    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 对象转包装类
     *
     * @param postComment
     * @return
     */
    public static PostCommentEsDTO objToDto(PostComment postComment) {
        if (postComment == null) {
            return null;
        }
        PostCommentEsDTO postCommentEsDTO = new PostCommentEsDTO();
        BeanUtils.copyProperties(postComment, postCommentEsDTO);
        return postCommentEsDTO;
    }

    /**
     * 包装类转对象
     *
     * @param postCommentEsDTO
     * @return
     */
    public static PostComment dtoToObj(PostCommentEsDTO postCommentEsDTO) {
        if (postCommentEsDTO == null) {
            return null;
        }
        PostComment postComment = new PostComment();
        BeanUtils.copyProperties(postCommentEsDTO, postComment);
        List<String> tagList = postCommentEsDTO.getTag();
        return postComment;
    }
}
