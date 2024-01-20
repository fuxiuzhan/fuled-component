package fuled.boot.example.dynamic.datasouce;

import com.fxz.fuled.service.annotation.EnableFuledBoot;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;

@EnableFuledBoot
@MapperScan(basePackages = "fuled.boot.example.dynamic.datasouce.mapper")
public class DynamicDatasouceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicDatasouceApplication.class, args);
    }

}
