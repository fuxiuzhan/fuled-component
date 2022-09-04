package com.fxz.fuled.common.dynamic.threadpool.reporter;


import com.fxz.fuled.common.dynamic.threadpool.pojo.ReporterDto;

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
