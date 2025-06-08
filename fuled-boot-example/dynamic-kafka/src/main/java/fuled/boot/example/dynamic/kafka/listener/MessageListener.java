package fuled.boot.example.dynamic.kafka.listener;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.fastjson.JSON;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.fxz.component.fuled.cat.starter.util.CatTraceCarrier;
import com.fxz.component.fuled.cat.starter.util.CatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.listener.BatchAcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

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
                //example for trace cross kafka
                for (Header header : datum.headers()) {
                    if (header.key().equalsIgnoreCase("traceContext")) {
                        CatTraceCarrier.Context context = JSON.parseObject(new String(header.value()), CatTraceCarrier.Context.class);
                        CatUtils.recoverySpan(context);
                        Transaction transaction = Cat.newTransaction("Service", "recv");
                        Cat.logEvent("Consume", datum.topic());
                        transaction.complete();
                        transaction.setStatus(Transaction.SUCCESS);
                    }
                }
            }
        }
        if (Objects.nonNull(acknowledgment)) {
            acknowledgment.acknowledge();
        }
    }
}
