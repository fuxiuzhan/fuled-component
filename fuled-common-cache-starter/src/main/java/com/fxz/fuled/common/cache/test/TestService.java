package com.fxz.fuled.common.cache.test;

import com.fxz.fuled.common.cache.annotation.Cache;
import org.springframework.stereotype.Component;

@Component
public class TestService {

    @Cache(localTurbo = true)
    public String test() {
        return "testtest";
    }
}
