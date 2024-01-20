# 参数解析




```html

<b>启动参数增加 vm option -Denv=DEV </b>
参数配置格式为fuled.dynamic.redis.名称.参数

注入redistemplate的bean名称对应关系：
master：
masterStringRedisTemplate和stringRedisTemplate
masterRedisTemplate和redisTemplate   
slave：
slaveStringRedisTemplate和slaveRedisTemplate

#配置如下
fuled.dynamic.redis.master=master
fuled.dynamic.redis.config.master.host=192.168.10.201
fuled.dynamic.redis.config.master.database=0
fuled.dynamic.redis.config.slave.host=192.168.10.201
fuled.dynamic.redis.config.slave.database=1

```
