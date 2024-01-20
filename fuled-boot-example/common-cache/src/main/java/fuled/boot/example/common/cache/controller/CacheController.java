package fuled.boot.example.common.cache.controller;

import fuled.boot.example.common.cache.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class CacheController {

    @Autowired
    private CacheService cacheService;

    @GetMapping("/cache")
    public List<String> cache(@RequestParam("key") String key) {
        return Arrays.asList(cacheService.getCache(key), cacheService.localCacheOnly(key));
    }
}
