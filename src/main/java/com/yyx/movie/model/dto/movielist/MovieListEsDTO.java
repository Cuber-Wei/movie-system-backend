package com.yyx.movie.model.dto.movielist;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.yyx.movie.model.entity.Movie;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子 ES 包装类
 **/
// todo 取消注释开启 ES（须先配置 ES）
//@Document(indexName = "movie")
@Data
public class MovieListEsDTO implements Serializable {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @Id
    private Long movieId;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String introduction;
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
     * @param movie
     * @return
     */
    public static MovieListEsDTO objToDto(Movie movie) {
        if (movie == null) {
            return null;
        }
        MovieListEsDTO movieEsDTO = new MovieListEsDTO();
        BeanUtils.copyProperties(movie, movieEsDTO);
        String tagsStr = movie.getTag();
        if (StringUtils.isNotBlank(tagsStr)) {
            movieEsDTO.setTag(JSONUtil.toList(tagsStr, String.class));
        }
        return movieEsDTO;
    }

    /**
     * 包装类转对象
     *
     * @param movieEsDTO
     * @return
     */
    public static Movie dtoToObj(MovieListEsDTO movieEsDTO) {
        if (movieEsDTO == null) {
            return null;
        }
        Movie movie = new Movie();
        BeanUtils.copyProperties(movieEsDTO, movie);
        List<String> tagList = movieEsDTO.getTag();
        if (CollUtil.isNotEmpty(tagList)) {
            movie.setTag(JSONUtil.toJsonStr(tagList));
        }
        return movie;
    }
}
