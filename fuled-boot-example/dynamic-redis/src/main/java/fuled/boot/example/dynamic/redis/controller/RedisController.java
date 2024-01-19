package fuled.boot.example.dynamic.redis.controller;


import fuled.boot.example.dynamic.redis.service.DynamicRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RedisController {
    @Autowired
    private DynamicRedisService dynamicRedisService;

    @GetMapping("/get")
    public List<String> getValue(@RequestParam("key") String key) {
        /**
         * 设置
         * master 1=1，slave不设置
         * 返回  ["1","1",null]
         */
        return dynamicRedisService.getValue(key);
    }
}
