package com.fxz.fuled.name.starter.selector;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fxz.fuled.common.chain.Filter;
import com.fxz.fuled.common.chain.Invoker;
import com.fxz.fuled.common.utils.Pair;

import java.util.List;

/**
 * 注册中心实例选择器，实例过滤扩展点
 */
public interface InstanceSelector extends Filter<Pair<String, List<Instance>>, List<Instance>> {

    String INSTANCE_FILTER_GROUP = "nacosInstanceFilterGroup";

    default List<Instance> select(Pair<String, List<Instance>> pair) {
        return pair.getSecond();
    }

    @Override
    default List<Instance> filter(Pair<String, List<Instance>> pair, Invoker<Pair<String, List<Instance>>, List<Instance>> invoker) {
        return invoker.invoke(new Pair<>(pair.getFirst(), select(pair)));
    }
}
