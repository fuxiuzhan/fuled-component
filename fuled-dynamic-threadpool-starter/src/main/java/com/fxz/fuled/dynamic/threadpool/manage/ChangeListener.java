package com.fxz.fuled.dynamic.threadpool.manage;

import com.fxz.fuled.dynamic.threadpool.pojo.ChangePair;

import java.util.List;

/**
 * 负责执行变更
 *
 * @author fxz
 */
public interface ChangeListener {

    void onChange(String threadPoolName, List<ChangePair> types);
}
