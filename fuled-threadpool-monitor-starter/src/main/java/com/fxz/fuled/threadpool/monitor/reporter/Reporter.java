package com.fxz.fuled.threadpool.monitor.reporter;

import com.fxz.fuled.threadpool.monitor.pojo.ReporterDto;

/**
 * @author fxz
 */
public interface Reporter {
    default void report(Class<? extends ReporterDto> record) {

    }
}
