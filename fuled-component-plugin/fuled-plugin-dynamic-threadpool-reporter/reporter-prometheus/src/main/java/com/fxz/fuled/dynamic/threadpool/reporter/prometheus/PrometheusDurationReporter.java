package com.fxz.fuled.dynamic.threadpool.reporter.prometheus;

import com.fxz.fuled.common.dynamic.threadpool.reporter.FastStatReporter;
import org.springframework.context.annotation.Primary;

import java.util.Objects;

/**
 * 队列等待及运行时间统计（非worker）
 */
@Primary
public class PrometheusDurationReporter implements FastStatReporter {

    private PrometheusReporter prometheusReporter;

    /**
     *
     * @param prometheusReporter
     */
    public PrometheusDurationReporter(PrometheusReporter prometheusReporter) {
        this.prometheusReporter = prometheusReporter;
    }

    @Override
    public void updateStat(String threadPoolName, long queuedDuration, long executeDuration) {
        if (Objects.nonNull(prometheusReporter)) {
            prometheusReporter.updateDuration(threadPoolName, queuedDuration, executeDuration);
        }
    }
}
