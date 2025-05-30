package fuled.boot.example.dynamic.kafka;

import com.fxz.component.fuled.cat.starter.annotation.EnableCatTracing;
import com.fxz.fuled.service.annotation.EnableFuledBoot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.kafka.core.KafkaTemplate;

@EnableFuledBoot
@EnableCatTracing
@Slf4j
public class DynamicKafka implements ApplicationRunner {

    @Autowired
    private KafkaTemplate kafkaTemplate;
    private String topic1 = "fxz_test1";
    private String topic2 = "fxz_test2";

    public static void main(String[] args) {
        SpringApplication.run(DynamicKafka.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        new Thread(() -> {
            while (true) {
                kafkaTemplate.send(topic1, System.currentTimeMillis() + "");
                kafkaTemplate.send(topic2, System.currentTimeMillis() + "");
                log.info("send");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
