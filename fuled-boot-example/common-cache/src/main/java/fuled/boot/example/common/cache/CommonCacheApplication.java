package fuled.boot.example.common.cache;

import com.fxz.fuled.common.cache.annotation.EnableCommonCache;
import com.fxz.fuled.dynamic.redis.annotation.EnableDynamicRedis;
import com.fxz.fuled.service.annotation.EnableFuledBoot;
import org.springframework.boot.SpringApplication;

@EnableFuledBoot
@EnableDynamicRedis
@EnableCommonCache
public class CommonCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommonCacheApplication.class, args);
    }

}
