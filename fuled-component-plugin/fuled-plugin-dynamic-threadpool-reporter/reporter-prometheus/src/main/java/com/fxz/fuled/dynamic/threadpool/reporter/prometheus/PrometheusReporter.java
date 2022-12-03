package com.fxz.fuled.dynamic.threadpool.reporter.prometheus;

import com.fxz.fuled.common.dynamic.threadpool.pojo.ReporterDto;
import com.fxz.fuled.common.dynamic.threadpool.reporter.Reporter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import sun.net.util.IPAddressUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PrometheusReporter implements Reporter {

    @Autowired(required = false)
    MeterRegistry meterRegistry;

    private static final String GAUGE = "fuled.dynamic.thread.pool";

    /**
     * tags
     */
    private static final String PREFIX = "thread.pool.";
    private static final String APP_NAME = PREFIX + "app.name";
    private static final String THREAD_POOL_NAME = PREFIX + "name";
    private static final String IPV4 = PREFIX + "ipv4";
    private static final String IPV6 = PREFIX + "ipv6";
    private static final String THREAD_POOL_TYPE = "type";
    private static final String QUEUE_TYPE = "queue.type";
    private static final String REJECT_TYPE = "reject.handler.type";
    /**
     * gauge
     */
    private static final String TIMESTAMP = PREFIX + "timestamp";
    private static final String CURRENT_CORE_SIZE = "current.core.size";
    private static final String REJECT_CNT = "reject.count";
    private static final String EXEC_COUNT = "exec.count";
    private static final String TASK_COUNT = "task.count";
    private static final String ACTIVE_COUNT = "active.count";
    private static final String MAX_QUEUE_SIZE = "max.queue.size";
    private static final String QUEUE_CAPACITY = "queue.capacity";
    private static final String CURRENT_QUEUE_SIZE = "current.queue.size";
    private static final String CORE_SIZE = "core.size";
    private static final String MAX_CORE_SIZE = "max.core.size";
    private Map<String, ReporterDto> threadPoolMap = new ConcurrentHashMap<>();

    /**
     * appName
     *
     * @param records
     */
    @Override
    public void report(List<ReporterDto> records) {
        if (!CollectionUtils.isEmpty(records) && Objects.nonNull(meterRegistry)) {
            Set<String> threadPools = records.stream().map(ReporterDto::getThreadPoolName).collect(Collectors.toSet());
            threadPools.forEach(t -> {
                ReporterDto r = records.stream().filter(e -> e.getThreadPoolName().equals(t)).max(Comparator.comparing(e -> e.getTimeStamp())).get();
                if (!threadPoolMap.containsKey(t)) {
                    ReporterDto reporterDto = new ReporterDto();
                    BeanUtils.copyProperties(r, reporterDto);
                    buildGauge(reporterDto);
                    threadPoolMap.put(t, reporterDto);
                } else {
                    ReporterDto reporterDto = threadPoolMap.get(t);
                    BeanUtils.copyProperties(r, reporterDto);
                }
            });
        }
    }

    public void buildGauge(ReporterDto reporterDto) {
        Gauge.builder(GAUGE + "." + TIMESTAMP, reporterDto, ReporterDto::getTimeStamp)
                .tags(buildTags(reporterDto))
                .description("Last Execute TimeStamp")
                .register(meterRegistry);

        Gauge.builder(GAUGE + "." + CURRENT_CORE_SIZE, reporterDto, ReporterDto::getCurrentPoolSize)
                .tags(buildTags(reporterDto))
                .description("ThreadPoolCoreSize")
                .register(meterRegistry);


        Gauge.builder(GAUGE + "." + REJECT_CNT, reporterDto, ReporterDto::getRejectCnt)
                .tags(buildTags(reporterDto))
                .description("ThreadPoolRejectCount")
                .register(meterRegistry);


        Gauge.builder(GAUGE + "." + EXEC_COUNT, reporterDto, ReporterDto::getExecCount)
                .tags(buildTags(reporterDto))
                .description("ThreadPoolExecCount")
                .register(meterRegistry);

        Gauge.builder(GAUGE + "." + TASK_COUNT, reporterDto, ReporterDto::getTaskCount)
                .tags(buildTags(reporterDto))
                .description("ThreadPoolTaskCount")
                .register(meterRegistry);

        Gauge.builder(GAUGE + "." + ACTIVE_COUNT, reporterDto, ReporterDto::getActiveCount)
                .tags(buildTags(reporterDto))
                .description("ThreadPoolActiveCount")
                .register(meterRegistry);

        Gauge.builder(GAUGE + "." + MAX_QUEUE_SIZE, reporterDto, ReporterDto::getQueueMaxSize)
                .tags(buildTags(reporterDto))
                .description("ThreadPoolMaxQueueSize")
                .register(meterRegistry);

        Gauge.builder(GAUGE + "." + QUEUE_CAPACITY, reporterDto, ReporterDto::getQueueMaxSize)
                .tags(buildTags(reporterDto))
                .description("ThreadPoolQueueCapacity")
                .register(meterRegistry);

        Gauge.builder(GAUGE + "." + CURRENT_QUEUE_SIZE, reporterDto, ReporterDto::getCurrentQueueSize)
                .tags(buildTags(reporterDto))
                .description("ThreadPoolCurrentQueueSize")
                .register(meterRegistry);

        Gauge.builder(GAUGE + "." + CORE_SIZE, reporterDto, ReporterDto::getCorePoolSize)
                .tags(buildTags(reporterDto))
                .description("ThreadPoolCoreSize")
                .register(meterRegistry);

        Gauge.builder(GAUGE + "." + MAX_CORE_SIZE, reporterDto, ReporterDto::getMaximumPoolSize)
                .tags(buildTags(reporterDto))
                .description("ThreadPoolMaxCoreSize")
                .register(meterRegistry);
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
