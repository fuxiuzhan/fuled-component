package com.fxz.fuled.dynamic.threadpool.reporter.prometheus;

import com.fxz.fuled.common.dynamic.threadpool.pojo.ReporterDto;
import com.fxz.fuled.common.dynamic.threadpool.reporter.Reporter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Summary;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import sun.net.util.IPAddressUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class PrometheusReporter implements Reporter {
    @Autowired(required = false)
    private MeterRegistry meterRegistry;
    @Autowired(required = false)
    private CollectorRegistry collectorRegistry;
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

    private static final String LARGEST_CORE_SIZE = "largest.pool.size";
    private static final String REJECT_CNT = "reject.count";
    private static final String EXEC_COUNT = "exec.count";
    private static final String TASK_COUNT = "task.count";
    private static final String ACTIVE_COUNT = "active.count";
    private static final String MAX_QUEUE_SIZE = "max.queue.size";
    private static final String QUEUE_CAPACITY = "queue.capacity";
    private static final String CURRENT_QUEUE_SIZE = "current.queue.size";
    private static final String CORE_SIZE = "core.size";
    private static final String MAX_CORE_SIZE = "max.core.size";
    private static final String QUEUED_DURATION = "queued.duration";
    private static final String EXECUTED_DURATION = "executed.duration";
    private Map<String, ReporterDto> reporterMap = new ConcurrentHashMap<>();
    private AtomicBoolean INIT = new AtomicBoolean(Boolean.FALSE);
    /**
     * waitTime
     */
    private Summary queuedSummary;
    /**
     * runningTime
     */
    private Summary executedSummary;

    /**
     * appName
     *
     * @param records
     */
    @Override
    public void report(List<ReporterDto> records) {
        if (!CollectionUtils.isEmpty(records) && Objects.nonNull(meterRegistry) && Objects.nonNull(collectorRegistry)) {
            Set<String> threadPools = records.stream().map(ReporterDto::getThreadPoolName).collect(Collectors.toSet());
            threadPools.forEach(t -> {
                ReporterDto r = records.stream().filter(e -> e.getThreadPoolName().equals(t)).max(Comparator.comparing(e -> e.getTimeStamp())).get();
                if (!reporterMap.containsKey(t)) {
                    ReporterDto reporterDto = new ReporterDto();
                    BeanUtils.copyProperties(r, reporterDto);
                    buildGauge(reporterDto);
                    reporterMap.put(t, reporterDto);
                } else {
                    ReporterDto reporterDto = reporterMap.get(t);
                    BeanUtils.copyProperties(r, reporterDto);
                }
            });
        }
    }

    /**
     * summary
     *
     * @param
     */
    public void updateDuration(String threadPoolName, long queuedDuration, long executeDuration) {
        if (reporterMap.containsKey(threadPoolName) && INIT.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {
            ReporterDto reporterDto = reporterMap.get(threadPoolName);
            queuedSummary = Summary.build((GAUGE + "." + QUEUED_DURATION).replace(".", "_"), "Thread Queued Time")
                    .quantile(0.99, 0.001).quantile(0.95, 0.005).quantile(0.90, 0.01)
                    .labelNames(buildLabels(reporterDto))
                    .register(collectorRegistry);
            executedSummary = Summary.build((GAUGE + "." + EXECUTED_DURATION).replace(".", "_"), "Thread Executed Time")
                    .quantile(0.99, 0.001).quantile(0.95, 0.005).quantile(0.90, 0.01)
                    .labelNames(buildLabels(reporterDto))
                    .register(collectorRegistry);
        }
        if (Objects.nonNull(queuedSummary) && Objects.nonNull(executedSummary)) {
            queuedSummary.labels(buildLabelValues(reporterMap.get(threadPoolName))).observe(queuedDuration);
            executedSummary.labels(buildLabelValues(reporterMap.get(threadPoolName))).observe(executeDuration);
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

        Gauge.builder(GAUGE + "." + LARGEST_CORE_SIZE, reporterDto, ReporterDto::getLargestPoolSize)
                .tags(buildTags(reporterDto))
                .description("ThreadLargestPoolSize")
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
     * 组装lables
     *
     * @param reporterDto
     * @return
     */
    private String[] buildLabels(ReporterDto reporterDto) {
        return new String[]{APP_NAME.replace(".", "_"), THREAD_POOL_NAME.replace(".", "_"), IPV4.replace(".", "_")
                , IPV6.replace(".", "_"), THREAD_POOL_TYPE.replace(".", "_")
                , QUEUE_TYPE.replace(".", "_"), REJECT_TYPE.replace(".", "_")};
    }

    /**
     * 组装labelValues
     *
     * @param reporterDto
     * @return
     */
    private String[] buildLabelValues(ReporterDto reporterDto) {
        return new String[]{reporterDto.getAppName(), reporterDto.getThreadPoolName(), buildIpString(reporterDto.getIps(), Boolean.TRUE)
                , buildIpString(reporterDto.getIps(), Boolean.FALSE), reporterDto.getThreadPoolType(), reporterDto.getQueueType(), reporterDto.getRejectHandlerType()};
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
