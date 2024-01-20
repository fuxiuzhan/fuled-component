package fuled.boot.example.dynamic.datasouce.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fuled.boot.example.dynamic.datasouce.entity.UserInfo;
import fuled.boot.example.dynamic.datasouce.mapper.base.BaseRepository;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author fxz
 */
@DS("master")
public interface UserRepository extends BaseRepository<UserInfo> {

    /**
     * 当entity只有构造方法创建时，会因为select的字段数量小于构造方法的参数数量而报错 indexOutRangeException
     *
     * @param id
     * @return
     */
//    @Cache(key = "#id")
    @Select("select user_name,age,addr from user_info where id=#{id}")
    UserInfo findById(Long id, Long id2);

    @Select("select user_name,age,addr from user_info")
    List<UserInfo> findPage(@Param("page") Page page);

    /**
     * mybatis 不支持方法名相同但参数或者返回值不同的方法，因为mapperStatementId=className+methodName
     *
     * @param id
     * @param deleted
     * @return
     */
    @DS("slave")
    @Select("select * from user_info where id=#{id} and deleted=#{deleted}")
    UserInfo findById(@Param("id") Long id, @Param("deleted") Integer deleted);
}
