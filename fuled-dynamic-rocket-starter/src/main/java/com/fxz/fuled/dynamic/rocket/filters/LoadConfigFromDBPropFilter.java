package com.fxz.fuled.dynamic.rocket.filters;

import com.fxz.fuled.common.chain.Filter;
import com.fxz.fuled.common.chain.Invoker;
import com.fxz.fuled.common.chain.annotation.FilterProperty;
import com.fxz.fuled.dynamic.rocket.config.DynamicRocketAutoConfig;
import com.fxz.fuled.dynamic.rocket.pojo.DynamicRocketProperties;

@FilterProperty(filterGroup = DynamicRocketAutoConfig.DYNAMIC_ROCKET_FILTER_GROUP, name = LoadConfigFromDBPropFilter.NAME)
public class LoadConfigFromDBPropFilter implements Filter<DynamicRocketProperties, Void> {

    public static final String NAME = "LoadConfigFromDBPropFilter";

    @Override
    public Void filter(DynamicRocketProperties dynamicKafkaProperties, Invoker<DynamicRocketProperties, Void> invoker) {
        //load config from db,cache etc...
        //decorate dynamicKafkaProperties
        return invoker.invoke(dynamicKafkaProperties);
    }
}
