package com.fxz.fuled.threadpool.monitor;

import com.fxz.fuled.threadpool.monitor.pojo.ChangePair;

import java.util.List;

/**
 * @author fxz
 */
public interface ChangeListener {

    void onChange(String threadPoolName, List<ChangePair> types);
}
