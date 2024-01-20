package fuled.boot.example.envencryptor.service;

import fuled.boot.example.envencryptor.config.TestProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ConfigService {

    ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(1);

    @Value("${fuled.props.test.name:A}")
    private String name;
    @Autowired
    private TestProperties testProperties;

    @PostConstruct
    public void init() {
        threadPoolExecutor.scheduleAtFixedRate(() -> log.info("name->{},TestProperties->{}", name, testProperties), 0, 1, TimeUnit.SECONDS);
    }
}
