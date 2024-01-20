package fuled.boot.example.dynamic.datasource;

import com.fxz.fuled.service.annotation.EnableFuledBoot;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;

@EnableFuledBoot
@MapperScan(basePackages = "fuled.boot.example.dynamic.datasource.mapper")
public class DynamicDatasourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicDatasourceApplication.class, args);
    }

}
