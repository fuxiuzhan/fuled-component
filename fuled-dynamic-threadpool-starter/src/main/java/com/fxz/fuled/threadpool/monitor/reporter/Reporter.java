package com.fxz.fuled.threadpool.monitor.reporter;

import com.fxz.fuled.threadpool.monitor.pojo.ReporterDto;

import java.util.List;

/**
 * 如果需要监控数据上报，则需要实现自己的reporter
 *
 * @author fxz
 */
public interface Reporter {
    default void report(List<ReporterDto> records) {
    }
}
