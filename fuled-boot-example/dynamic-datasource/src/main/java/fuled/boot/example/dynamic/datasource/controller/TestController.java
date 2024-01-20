package fuled.boot.example.dynamic.datasource.controller;


import com.alibaba.fastjson.JSON;
import fuled.boot.example.dynamic.datasource.entity.UserInfo;
import fuled.boot.example.dynamic.datasource.mapper.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/find")
    public String findById(@RequestParam("id") Long id) {
        UserInfo byId = userRepository.findById(id, 0);
        //byId.getPhone().getValue();
        return JSON.toJSONString(byId);
    }
}
