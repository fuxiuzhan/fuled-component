package fuled.boot.example.dynamic.datasource.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import fuled.boot.example.dynamic.datasource.entity.UserInfo;
import fuled.boot.example.dynamic.datasource.mapper.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@RestController
public class TestController {

    @Autowired(required = false)
    private DataSource dataSource;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/find")
    public String findById(@RequestParam("id") Long id) throws Exception {
        for (int x = 0; x < 1000; x++) {
            for (int y = 0; y < 1000; y++) {
                UserInfo byId = userRepository.findById((long) y, 0);
            }
            //byId.getPhone().getValue();
            if (dataSource instanceof DynamicRoutingDataSource) {
                ((DynamicRoutingDataSource) dataSource).afterPropertiesSet();
            }
        }

        return JSON.toJSONString("byId");
    }
}
