package com.yyx.movie.model.vo;

import cn.hutool.json.JSONUtil;
import com.yyx.movie.model.entity.Movie;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子视图
 */
@Data
public class MovieVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long movieId;
    /**
     * 电影名称
     */
    private String title;
    /**
     * 电影介绍
     */
    private String introduction;
    /**
     * 演员信息
     */
    private String actors;
    /**
     * 时长
     */
    private Integer duration;
    /**
     * 标签列表（json 数组）
     */
    private List<String> tag;
    /**
     * 评论数
     */
    private Long commentNum;
    /**
     * 出版日期
     */
    private Date publishDate;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 包装类转对象
     *
     * @param movieVO
     * @return
     */
    public static Movie voToObj(MovieVO movieVO) {
        if (movieVO == null) {
            return null;
        }
        Movie movie = new Movie();
        BeanUtils.copyProperties(movieVO, movie);
        List<String> tagList = movieVO.getTag();
        movie.setTag(JSONUtil.toJsonStr(tagList));
        return movie;
    }

    /**
     * 对象转包装类
     *
     * @param movie
     * @return
     */
    public static MovieVO objToVo(Movie movie) {
        if (movie == null) {
            return null;
        }
        MovieVO movieVO = new MovieVO();
        BeanUtils.copyProperties(movie, movieVO);
        movieVO.setTag(JSONUtil.toList(movie.getTag(), String.class));
        return movieVO;
    }
}
