package com.fxz.fuled.threadpool.monitor.reporter;

import com.fxz.fuled.threadpool.monitor.pojo.ReporterDto;

import java.util.List;

/**
 * @author fxz
 */
public interface Reporter {
    default void report(List<ReporterDto> records) {
    }
}
