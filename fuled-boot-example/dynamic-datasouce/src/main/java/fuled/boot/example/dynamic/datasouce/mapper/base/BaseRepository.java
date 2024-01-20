package fuled.boot.example.dynamic.datasouce.mapper.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fuled.boot.example.dynamic.datasouce.entity.base.BaseEntity;

/**
 * @author fxz
 */
public interface BaseRepository<T extends BaseEntity> extends BaseMapper<T> {
}
