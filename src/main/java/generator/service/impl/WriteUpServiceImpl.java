package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.l0v3ch4n.oj.model.entity.WriteUp;
import generator.service.WriteUpService;
import generator.mapper.WriteUpMapper;
import org.springframework.stereotype.Service;

/**
* @author Lovechan
* @description 针对表【write_up(题解)】的数据库操作Service实现
* @createDate 2024-11-09 18:19:23
*/
@Service
public class WriteUpServiceImpl extends ServiceImpl<WriteUpMapper, WriteUp>
    implements WriteUpService{

}




