package fuled.boot.example.dynamic.datasource;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.fxz.fuled.service.annotation.EnableFuledBoot;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Import;

@EnableFuledBoot
@MapperScan(basePackages = "fuled.boot.example.dynamic.datasource.mapper")
@Import(DynamicDataSourceAutoConfiguration.class)
public class DynamicDatasourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicDatasourceApplication.class, args);
    }

}
