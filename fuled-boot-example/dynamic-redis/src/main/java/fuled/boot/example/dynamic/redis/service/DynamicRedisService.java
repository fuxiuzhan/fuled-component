package fuled.boot.example.dynamic.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DynamicRedisService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    @Qualifier("masterStringRedisTemplate")
    StringRedisTemplate stringRedisTemplateMaster;
    @Autowired
    @Qualifier("slaveStringRedisTemplate")
    StringRedisTemplate stringRedisTemplateSlave;

    public List<String> getValue(String key) {
        return Arrays.asList(stringRedisTemplate.opsForValue().get(key), stringRedisTemplateMaster.opsForValue().get(key), stringRedisTemplateSlave.opsForValue().get(key));
    }

}
