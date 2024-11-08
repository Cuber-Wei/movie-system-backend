package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.l0v3ch4n.oj.model.entity.PostComment;
import generator.service.PostCommentService;
import generator.mapper.PostCommentMapper;
import org.springframework.stereotype.Service;

/**
* @author Lovechan
* @description 针对表【post_comment(帖子评论)】的数据库操作Service实现
* @createDate 2024-11-09 18:19:23
*/
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment>
    implements PostCommentService{

}




