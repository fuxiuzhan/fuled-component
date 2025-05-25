package com.fxz.fuled.name.starter.selector;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fxz.fuled.common.chain.Filter;
import com.fxz.fuled.common.chain.Invoker;

import java.util.List;

/**
 * 注册中心实例选择器，实例过滤扩展点
 */
public interface InstanceSelector extends Filter<List<Instance>, List<Instance>> {

    String INSTANCE_FILTER_GROUP = "nacosInstanceFilterGroup";

    default List<Instance> select(List<Instance> instanceList) {
        return instanceList;
    }

    @Override
    default List<Instance> filter(List<Instance> instanceList, Invoker<List<Instance>, List<Instance>> invoker) {
        return invoker.invoke(select(instanceList));
    }
}
