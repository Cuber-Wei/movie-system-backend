package com.l0v3ch4n.oj.model.dto.writeup;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.l0v3ch4n.oj.model.entity.WriteUp;
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
 * 题解 ES 包装类
 **/
// todo 取消注释开启 ES（须先配置 ES）
//@Document(indexName = "writeUp")
@Data
public class WriteUpEsDTO implements Serializable {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * id
     */
    @Id
    private Long writeUpId;

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

    private static final long serialVersionUID = 1L;

    /**
     * 对象转包装类
     *
     * @param writeUp
     * @return
     */
    public static WriteUpEsDTO objToDto(WriteUp writeUp) {
        if (writeUp == null) {
            return null;
        }
        WriteUpEsDTO writeUpEsDTO = new WriteUpEsDTO();
        BeanUtils.copyProperties(writeUp, writeUpEsDTO);
        String tagsStr = writeUp.getTag();
        if (StringUtils.isNotBlank(tagsStr)) {
            writeUpEsDTO.setTag(JSONUtil.toList(tagsStr, String.class));
        }
        return writeUpEsDTO;
    }

    /**
     * 包装类转对象
     *
     * @param writeUpEsDTO
     * @return
     */
    public static WriteUp dtoToObj(WriteUpEsDTO writeUpEsDTO) {
        if (writeUpEsDTO == null) {
            return null;
        }
        WriteUp writeUp = new WriteUp();
        BeanUtils.copyProperties(writeUpEsDTO, writeUp);
        List<String> tagList = writeUpEsDTO.getTag();
        if (CollUtil.isNotEmpty(tagList)) {
            writeUp.setTag(JSONUtil.toJsonStr(tagList));
        }
        return writeUp;
    }
}
