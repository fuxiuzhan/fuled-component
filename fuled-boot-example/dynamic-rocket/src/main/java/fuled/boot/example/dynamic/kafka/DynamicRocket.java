package fuled.boot.example.dynamic.kafka;

import com.fxz.component.fuled.cat.starter.annotation.EnableCatTracing;
import com.fxz.fuled.service.annotation.EnableFuledBoot;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.messaging.support.MessageBuilder;

@EnableFuledBoot
@EnableCatTracing
@Slf4j
public class DynamicRocket implements ApplicationRunner {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    private String topic1 = "test-a";
    private String topic2 = "test-b";

    public static void main(String[] args) {
        SpringApplication.run(DynamicRocket.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        new Thread(() ->
        {
            while (true) {
                try {
                    Thread.sleep(1000);
//                    rocketMQTemplate.send(topic1, MessageBuilder.withPayload(topic1 + ":" + System.currentTimeMillis()).build());
//                    rocketMQTemplate.send(topic2, MessageBuilder.withPayload(topic2 + ":" + System.currentTimeMillis()).build());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
