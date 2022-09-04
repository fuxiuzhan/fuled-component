package com.fxz.fuled.dynamic.threadpool.manage;


import com.fxz.fuled.common.dynamic.threadpool.pojo.ReporterDto;

/**
 * 负责执行统计
 *
 * @author fxz
 */
public interface Recordable {

    ReporterDto getRecord();
}
