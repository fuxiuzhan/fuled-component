package fuled.boot.example.dynamic.redis;

import com.fxz.fuled.dynamic.redis.annotation.EnableDynamicRedis;
import com.fxz.fuled.service.annotation.EnableFuledBoot;
import org.springframework.boot.SpringApplication;


@EnableFuledBoot
@EnableDynamicRedis
public class DynamicRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicRedisApplication.class, args);
    }

}
