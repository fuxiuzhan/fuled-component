package com.fxz.fuled.dynamic.kafka.filters;

import com.fxz.fuled.common.chain.Filter;
import com.fxz.fuled.common.chain.Invoker;
import com.fxz.fuled.common.chain.annotation.FilterProperty;
import com.fxz.fuled.dynamic.kafka.config.DynamicKafkaAutoConfig;
import com.fxz.fuled.dynamic.kafka.pojo.DynamicKafkaProperties;

@FilterProperty(filterGroup = DynamicKafkaAutoConfig.DYNAMIC_KAFKA_FILTER_GROUP, name = LoadConfigFromDBPropFilter.NAME)
public class LoadConfigFromDBPropFilter implements Filter<DynamicKafkaProperties, Void> {

    public static final String NAME = "LoadConfigFromDBPropFilter";

    @Override
    public Void filter(DynamicKafkaProperties dynamicKafkaProperties, Invoker<DynamicKafkaProperties, Void> invoker) {
        //load config from db,cache etc...
        //decorate dynamicKafkaProperties
        return invoker.invoke(dynamicKafkaProperties);
    }
}
