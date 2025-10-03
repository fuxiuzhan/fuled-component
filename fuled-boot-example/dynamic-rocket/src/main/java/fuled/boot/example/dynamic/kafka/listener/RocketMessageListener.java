package fuled.boot.example.dynamic.kafka.listener;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service(RocketMessageListener.ListenerBeanName)
public class RocketMessageListener implements RocketMQListener<MessageExt> {
    public static final String ListenerBeanName = "defaultMessageListener";


    @Override
    public void onMessage(MessageExt messageExt) {
        byte[] body = messageExt.getBody();
        String messageBody = new String(body); // 字符串消息直接转
        // 如果是对象消息，可使用 JSON 工具反序列化（如 Jackson）
        // User user = new ObjectMapper().readValue(body, User.class);
        // 2. 获取消息元数据
        String topic = messageExt.getTopic(); // 消息所属的 Topic
        long offset = messageExt.getQueueOffset(); // 消息在队列中的偏移量
        int queueId = messageExt.getQueueId(); // 消息所在的队列 ID
        String tags = messageExt.getTags(); // 消息的 Tag
        String keys = messageExt.getKeys(); // 消息的 Key（用于查询）
        long bornTimestamp = messageExt.getBornTimestamp(); // 消息生成时间戳（毫秒）
        String bornHost = messageExt.getBornHostString(); // 消息发送者地址
        long storeTimestamp = messageExt.getStoreTimestamp(); // 消息存储时间戳（毫秒）
        int reconsumeTimes = messageExt.getReconsumeTimes(); // 消息重试次数
        String msgId = messageExt.getMsgId(); // 消息唯一 ID（Broker 生成）
        String transactionId = messageExt.getTransactionId(); // 事务消息 ID（若为事务消息）
        log.info("recv->{}", messageBody);
    }
}
