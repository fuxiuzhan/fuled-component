package com.fxz.fuled.name.starter.selector;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fxz.fuled.common.utils.Pair;

import java.util.List;

public class InstanceWarmUpSelectBronTime extends AbsInstanceSelector {
    @Override
    public List<Instance> select(Pair<String, List<Instance>> pair) {
        /**
         * 根据注册中心meta数据中的启动时间，暂时过滤，或者使用loadbalancer接口进行权重调整
         */
        return super.select(pair);
    }

    @Override
    public String name() {
        return "InstanceWarmUpSelectBronTime";
    }

    @Override
    public int order() {
        return -10;
    }
}
