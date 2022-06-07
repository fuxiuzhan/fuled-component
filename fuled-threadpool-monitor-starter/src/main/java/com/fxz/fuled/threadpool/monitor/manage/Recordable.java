package com.fxz.fuled.threadpool.monitor.manage;

import com.fxz.fuled.threadpool.monitor.pojo.ReporterDto;

/**
 * 负责执行统计
 *
 * @author fxz
 */
public interface Recordable {

    ReporterDto getRecord();
}
