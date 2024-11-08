package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.l0v3ch4n.oj.model.entity.Post;
import generator.service.PostService;
import generator.mapper.PostMapper;
import org.springframework.stereotype.Service;

/**
* @author Lovechan
* @description 针对表【post(帖子)】的数据库操作Service实现
* @createDate 2024-11-09 18:19:23
*/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{

}




