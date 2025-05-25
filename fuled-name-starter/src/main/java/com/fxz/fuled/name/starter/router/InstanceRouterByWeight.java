package com.fxz.fuled.name.starter.router;

import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InstanceRouterByWeight extends RoundRobinRule {

    @Override
    public Server choose(ILoadBalancer lb, Object key) {
        //replace round
        return super.choose(lb, key);
    }
}
