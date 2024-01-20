package fuled.boot.example.envencryptor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "fuled.props.test")
public class TestProperties {

    /**
     * 普通字段
     */
    private String name;

    /**
     * 配置加密字段，可用于数据库，redis ak/sk等的加密
     */
    private String password;
}
