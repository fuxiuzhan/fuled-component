package com.fxz.fuled.dynamic.rocket.pojo;

import lombok.Data;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.SelectorType;

@Data
public class ConsumerProperties {

    public static final String NAME_SERVER_PLACEHOLDER = "${rocketmq.name-server:}";
    public static final String ACCESS_KEY_PLACEHOLDER = "${rocketmq.consumer.access-key:}";
    public static final String SECRET_KEY_PLACEHOLDER = "${rocketmq.consumer.secret-key:}";
    public static final String TRACE_TOPIC_PLACEHOLDER = "${rocketmq.consumer.customized-trace-topic:}";
    public static final String ACCESS_CHANNEL_PLACEHOLDER = "${rocketmq.access-channel:}";

    private String consumerGroup;
    private String topic;
    private SelectorType selectorType = SelectorType.TAG;
    private String selectorExpression = "*";
    private ConsumeMode consumeMode = ConsumeMode.CONCURRENTLY;
    private MessageModel messageModel = MessageModel.CLUSTERING;
    @Deprecated
    private int consumeThreadMax = 64;
    private int consumeThreadNumber = 20;
    private int maxReconsumeTimes = -1;
    private long consumeTimeout = 15L;
    private int replyTimeout = 3000;
    private String accessKey = ACCESS_KEY_PLACEHOLDER;
    private String secretKey = SECRET_KEY_PLACEHOLDER;
    private boolean enableMsgTrace = Boolean.FALSE;
    private String customizedTraceTopic = TRACE_TOPIC_PLACEHOLDER;
    private String nameServer = NAME_SERVER_PLACEHOLDER;
    private String accessChannel = ACCESS_CHANNEL_PLACEHOLDER;
    private String tlsEnable = "false";
    private String namespace = "";
    private int delayLevelWhenNextConsume = 0;
    private int suspendCurrentQueueTimeMillis = 1000;
    private int awaitTerminationMillisWhenShutdown = 1000;
    private String instanceName = "DEFAULT";
//    public ConsumerProperties(String consumerGroup, String topic) {
//        this.consumerGroup = consumerGroup;
//        this.topic = topic;
//    }
}
