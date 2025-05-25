package com.fxz.fuled.name.starter.selector;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fxz.fuled.common.chain.annotation.FilterProperty;
import com.fxz.fuled.common.utils.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@FilterProperty(filterGroup = InstanceSelector.INSTANCE_FILTER_GROUP, name = "InstanceSelectByTag", order = 0)
public class InstanceSelectByTag implements InstanceSelector {

    @Value("#{${fuled.discovery.insatnce.selector.tag:{}}}")
    private Map<String, Map<String, String>> metaTag = new HashMap<>();

    @Override
    public List<Instance> select(Pair<String, List<Instance>> pair) {
        if (!CollectionUtils.isEmpty(metaTag) && !CollectionUtils.isEmpty(pair.getSecond()) && metaTag.containsKey(pair.getFirst())) {
            /**
             * tag逐个匹配
             */
            Map<String, String> singleAppMeta = metaTag.get(pair.getFirst());
            return pair.getSecond().stream().filter(i -> {
                for (Map.Entry<String, String> singleTag : singleAppMeta.entrySet()) {
                    if (!singleTag.getValue().equalsIgnoreCase(i.getMetadata().get(singleTag.getKey()))) {
                        return Boolean.FALSE;
                    }
                }
                return Boolean.TRUE;
            }).collect(Collectors.toList());
        }
        return pair.getSecond();
    }
}
