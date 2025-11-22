package com.yyx.movie.model.vo;

import com.yyx.movie.model.entity.Movie;
import com.yyx.movie.model.entity.MovieList;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子视图
 */
@Data
public class MovieListVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long movieListId;
    /**
     * 电榜单标题
     */
    private String title;
    /**
     * 榜单介绍
     */
    private String introduction;
    /**
     * 创建用户 id
     */
    private Long userId;
    /**
     * 榜单创建人
     */
    private UserVO user;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 榜单中电影数量
     */
    private Long movieNum;

    /**
     * 包装类转对象
     *
     * @param movieListVO
     * @return
     */
    public static Movie voToObj(MovieListVO movieListVO) {
        if (movieListVO == null) {
            return null;
        }
        Movie movie = new Movie();
        BeanUtils.copyProperties(movieListVO, movie);
        return movie;
    }

    /**
     * 对象转包装类
     *
     * @param movieList
     * @return
     */
    public static MovieListVO objToVo(MovieList movieList) {
        if (movieList == null) {
            return null;
        }
        MovieListVO movieListVO = new MovieListVO();
        BeanUtils.copyProperties(movieList, movieListVO);
        return movieListVO;
    }
}
