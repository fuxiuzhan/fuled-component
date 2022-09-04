package com.fxz.fuled.dynamic.threadpool.reporter.prometheus;

import com.fxz.fuled.common.dynamic.threadpool.pojo.ReporterDto;
import com.fxz.fuled.common.dynamic.threadpool.reporter.Reporter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import sun.net.util.IPAddressUtil;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PrometheusReporter implements Reporter {

    @Autowired
    MeterRegistry meterRegistry;

    private static final String GAUGE = "fuled.dynamic.thread.pool";

    /**
     * tags
     */
    private static final String PREFIX = "thread.pool.";
    private static final String APP_NAME = PREFIX + "app.name";
    private static final String THREAD_POOL_NAME = PREFIX + "thread.pool.name";
    private static final String IPV4 = PREFIX + "ipv4";
    private static final String IPV6 = PREFIX + "ipv6";
    private static final String THREAD_POOL_TYPE = "thread.pool.type";
    private static final String QUEUE_TYPE = "queue.type";
    private static final String REJECT_TYPE = "reject.handler.type";

    /**
     * gauge
     */
    private static final String TIMESTAMP = PREFIX + "timestamp";
    private static final String CURRENT_CORE_SIZE = "current.core.size";
    private static final String REJECT_CNT = "reject.count";
    private static final String EXEC_COUNT = "exec.count";
    private static final String MAX_QUEUE_SIZE = "max.queue.size";
    private static final String QUEUE_CAPACITY = "queue.capacity";
    private static final String CURRENT_QUEUE_SIZE = "current.queue.size";
    private static final String CORE_SIZE = "core.size";
    private static final String MAX_CORE_SIZE = "max.core.size";

    /**
     * appName
     *
     * @param records
     */
    @Override
    public void report(List<ReporterDto> records) {
        if (!CollectionUtils.isEmpty(records)) {
            records.forEach(r -> {
                meterRegistry.gauge(GAUGE, Tags.concat(buildTags(r), TIMESTAMP), r.getTimeStamp());
                meterRegistry.gauge(GAUGE, Tags.concat(buildTags(r), CURRENT_CORE_SIZE), r.getCurrentPoolSize());
                meterRegistry.gauge(GAUGE, Tags.concat(buildTags(r), REJECT_CNT), r.getRejectCnt());
                meterRegistry.gauge(GAUGE, Tags.concat(buildTags(r), EXEC_COUNT), r.getExecCount());
                meterRegistry.gauge(GAUGE, Tags.concat(buildTags(r), MAX_QUEUE_SIZE), r.getQueueMaxSize());
                meterRegistry.gauge(GAUGE, Tags.concat(buildTags(r), QUEUE_CAPACITY), r.getQueueMaxSize());
                meterRegistry.gauge(GAUGE, Tags.concat(buildTags(r), CURRENT_QUEUE_SIZE), r.getCurrentQueueSize());
                meterRegistry.gauge(GAUGE, Tags.concat(buildTags(r), CORE_SIZE), r.getCorePoolSize());
                meterRegistry.gauge(GAUGE, Tags.concat(buildTags(r), MAX_CORE_SIZE), r.getMaximumPoolSize());
            });
        }
    }

    /**
     * 创建Gauge 并添加tags
     *
     * @param reporterDto
     * @return
     */
    private Iterable<Tag> buildTags(ReporterDto reporterDto) {
        return Tags.concat(Tags.empty(), APP_NAME, reporterDto.getAppName(), THREAD_POOL_NAME, reporterDto.getThreadPoolName(),
                IPV4, buildIpString(reporterDto.getIps(), Boolean.TRUE), IPV6, buildIpString(reporterDto.getIps(), Boolean.FALSE),
                THREAD_POOL_TYPE, reporterDto.getThreadPoolType(), QUEUE_TYPE, reporterDto.getQueueType(),
                REJECT_TYPE, reporterDto.getRejectHandlerType());
    }

    /**
     * 构建ip地址列表
     *
     * @param ips
     * @param ipv4
     * @return
     */
    private String buildIpString(List<String> ips, boolean ipv4) {
        List<String> result = ips.stream().filter(i -> IPAddressUtil.isIPv4LiteralAddress(i) == ipv4).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(result)) {
            return "";
        }
        return String.join(",", result);
    }
}
