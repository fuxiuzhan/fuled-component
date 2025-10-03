package fuled.boot.example.dynamic.kafka.listener;


import com.fxz.fuled.dynamic.rocket.anno.DynamicMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@DynamicMessageListener(listeners = {
        @RocketMQMessageListener(consumerGroup = "gid_test-a_anno", topic = "test-a"),
        @RocketMQMessageListener(consumerGroup = "gid_test-b_anno", topic = "test-b")
})
@Slf4j
@Service
public class AnnoRocketMessageListener implements RocketMQListener<MessageExt> {
    @Override
    public void onMessage(MessageExt message) {
        log.info("recv from anno->{}", new String(message.getBody()));
    }
}
