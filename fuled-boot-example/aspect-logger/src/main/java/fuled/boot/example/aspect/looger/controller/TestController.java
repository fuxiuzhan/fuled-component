package fuled.boot.example.aspect.looger.controller;

import com.fxz.fuled.logger.starter.annotation.Monitor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class TestController {


    @GetMapping("/test")
    @Monitor(printParams = true, printResult = true)
    public List<String> cache(@RequestParam("key") String key) {
        return Arrays.asList("1", "2");
    }
}
