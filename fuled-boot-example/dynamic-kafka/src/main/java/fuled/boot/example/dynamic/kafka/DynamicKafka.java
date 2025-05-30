package fuled.boot.example.dynamic.kafka;

import com.alibaba.fastjson.JSON;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.fxz.component.fuled.cat.starter.annotation.EnableCatTracing;
import com.fxz.component.fuled.cat.starter.util.CatTraceCarrier;
import com.fxz.component.fuled.cat.starter.util.CatUtils;
import com.fxz.fuled.service.annotation.EnableFuledBoot;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.List;

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
                Transaction transaction = Cat.newTransaction("Service", "sendKafka");
                List<Header> headers = new ArrayList<>();
                CatTraceCarrier.Context crossKafka = CatUtils.createSpan("crossKafka", topic1);
                headers.add(new RecordHeader("traceContext", JSON.toJSONString(crossKafka).getBytes()));
                kafkaTemplate.send(new ProducerRecord(topic1, 0, "key", System.currentTimeMillis() + "", headers));
//                kafkaTemplate.send(topic1, System.currentTimeMillis() + "");
                kafkaTemplate.send(topic2, System.currentTimeMillis() + "");
                log.info("send");
                transaction.complete();
                transaction.setStatus(Transaction.SUCCESS);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
