# 通用多层次可扩展缓存

```html
内置缓存路径：

proxy->localCache(lru)->redisCache->targrt

其中是否使用localCache由参数localTurbo 控制
redisCache使用容器内的redisTemplate作为cacheContainer

自定义扩展参见

<href>com.fxz.fuled.common.cache.filters.RedisCacheFilter</href>
<href>com.fxz.fuled.common.cache.container.RedisCacheContainer</href>

主要实现自定义CacheFilter和CacheContainer注入容器即可


2024-01-20 00:14:01.257  INFO 67251 --- [           main] com.fxz.fuled.common.chain.FilterConfig  : Filter->RedisCacheFilter group->fuled.common.cache.group order->134217727 enabled->true adding....
2024-01-20 00:14:01.257  INFO 67251 --- [           main] com.fxz.fuled.common.chain.FilterConfig  : Filter->RedisCacheFilter group->fuled.common.cache.group order->134217727 enabled->true added
2024-01-20 00:14:01.257  INFO 67251 --- [           main] com.fxz.fuled.common.chain.FilterConfig  : Filter->LocalCacheFilter group->fuled.common.cache.group order->536870911 enabled->true adding....
2024-01-20 00:14:01.257  INFO 67251 --- [           main] com.fxz.fuled.common.chain.FilterConfig  : Filter->LocalCacheFilter group->fuled.common.cache.group order->536870911 enabled->true added
2024-01-20 00:14:01.257  INFO 67251 --- [           main] com.fxz.fuled.common.chain.FilterConfig  : Filter->PrepareCacheFilter group->fuled.common.cache.group order->2147483647 enabled->true adding....
2024-01-20 00:14:01.257  INFO 67251 --- [           main] com.fxz.fuled.common.chain.FilterConfig  : Filter->PrepareCacheFilter group->fuled.common.cache.group order->2147483647 enabled->true added
2024-01-20 00:14:01.257  INFO 67251 --- [           main] com.fxz.fuled.common.chain.FilterConfig  : Filter->ProceedCacheFilter group->fuled.common.cache.group order->-2147483648 enabled->true adding....
2024-01-20 00:14:01.257  INFO 67251 --- [           main] com.fxz.fuled.common.chain.FilterConfig  : Filter->ProceedCacheFilter group->fuled.common.cache.group order->-2147483648 enabled->true added

日志当中可以看到自定义cache被自动处理即可

```
