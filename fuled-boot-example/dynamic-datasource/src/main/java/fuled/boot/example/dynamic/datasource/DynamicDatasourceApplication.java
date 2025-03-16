package fuled.boot.example.dynamic.datasource;

import com.fxz.fuled.dynamic.datasource.starter.annotation.DynamicDsConfig;
import com.fxz.fuled.service.annotation.EnableFuledBoot;
import fuled.boot.example.dynamic.datasource.mapper2.UserRepository2;
import fuled.boot.example.dynamic.datasource.mapper3.UserRepository3;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;

import java.lang.reflect.Proxy;

@Slf4j
@EnableFuledBoot
@MapperScan(basePackages = "fuled.boot.example.dynamic.datasource")
@DynamicDsConfig(rootClass = Proxy.class, config = {
        @DynamicDsConfig.PackageConfig(packages = {"fuled.boot.example.dynamic.datasource.mapper2"}, ds = "slave"),
        @DynamicDsConfig.PackageConfig(packages = {"fuled.boot.example.dynamic.datasource.mapper3"}, ds = "third")})
public class DynamicDatasourceApplication implements ApplicationRunner {
    @Autowired
    private UserRepository2 userRepository2;
    @Autowired
    private UserRepository3 userRepository3;

    public static void main(String[] args) {
        SpringApplication.run(DynamicDatasourceApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        new Thread(() -> {
            while (true) {
                log.info("userRepository2->{}", userRepository2.selectById(1));
                log.info("userRepository3->{}", userRepository3.selectById(1));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
