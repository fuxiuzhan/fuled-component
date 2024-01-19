package fuled.boot.example.common.cache.service;

import com.fxz.fuled.common.cache.annotation.BatchCache;
import com.fxz.fuled.common.cache.annotation.Cache;
import com.fxz.fuled.common.cache.annotation.CacheParam;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 *
 */
@Component
public class CacheService {

    /**
     * 缓存执行路径 proxy->redisCache->target
     *
     * @param key
     * @return
     */
    @BatchCache(caches = {
            @Cache(key = "'key_prefix_'+#arg0"),
            @Cache(key = "'key_prefix_'+#key"),
            @Cache(expr = 20, unit = TimeUnit.HOURS)
    })
    public String getCache(@CacheParam("key") String key) {
        return "value";
    }

    /**
     * 缓存执行路径 proxy->localCache->redisCache->target
     *
     * @param key
     * @return
     */
    @Cache(localTurbo = true)
    public String localCacheOnly(String key) {
        return "local";
    }

    @Cache(clearLocal = true)
    public void clear() {
        //do noting
    }
}
