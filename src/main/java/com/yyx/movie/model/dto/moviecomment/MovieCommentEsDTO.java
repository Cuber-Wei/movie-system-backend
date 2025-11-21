package com.yyx.movie.model.dto.moviecomment;

import com.yyx.movie.model.entity.MovieComment;
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
//@Document(indexName = "movieComment")
@Data
public class MovieCommentEsDTO implements Serializable {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @Id
    private Long movieCommentId;
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
     * @param movieComment
     * @return
     */
    public static MovieCommentEsDTO objToDto(MovieComment movieComment) {
        if (movieComment == null) {
            return null;
        }
        MovieCommentEsDTO movieCommentEsDTO = new MovieCommentEsDTO();
        BeanUtils.copyProperties(movieComment, movieCommentEsDTO);
        return movieCommentEsDTO;
    }

    /**
     * 包装类转对象
     *
     * @param movieCommentEsDTO
     * @return
     */
    public static MovieComment dtoToObj(MovieCommentEsDTO movieCommentEsDTO) {
        if (movieCommentEsDTO == null) {
            return null;
        }
        MovieComment movieComment = new MovieComment();
        BeanUtils.copyProperties(movieCommentEsDTO, movieComment);
        List<String> tagList = movieCommentEsDTO.getTag();
        return movieComment;
    }
}
