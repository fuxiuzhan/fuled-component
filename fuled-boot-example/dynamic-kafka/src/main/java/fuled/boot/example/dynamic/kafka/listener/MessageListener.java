package fuled.boot.example.dynamic.kafka.listener;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.BatchAcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service(MessageListener.ListenerBeanName)
public class MessageListener implements BatchAcknowledgingMessageListener<String, String> {
    public static final String ListenerBeanName = "batchMessageListener";


    @SentinelResource("kafkaConsumer")
    @Override
    public void onMessage(List<ConsumerRecord<String, String>> data, Acknowledgment acknowledgment) {
        if (!CollectionUtils.isEmpty(data)) {
            for (ConsumerRecord<String, String> datum : data) {
                log.info("topic->{},offset->{},partition->{},data->{}", datum.topic(), datum.offset(), datum.partition(), datum.value());
            }
        }
    }
}
