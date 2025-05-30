package com.fxz.fuled.name.starter.selector;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fxz.fuled.common.utils.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class InstanceSelectByTag extends AbsInstanceSelector {

    @Value("#{${fuled.discovery.insatnce.selector.tag:{}}}")
    private Map<String, Map<String, String>> metaTag = new HashMap<>();

    @Override
    public List<Instance> select(Pair<String, List<Instance>> pair) {
        if (!CollectionUtils.isEmpty(metaTag) && metaTag.containsKey(pair.getFirst()) && !CollectionUtils.isEmpty(pair.getSecond())) {
            /**
             * tag逐个匹配
             */
            Map<String, String> singleAppMeta = metaTag.get(pair.getFirst());
            List<Instance> collect = pair.getSecond().stream().filter(i -> {
                for (Map.Entry<String, String> singleTag : singleAppMeta.entrySet()) {
                    if (!singleTag.getValue().equalsIgnoreCase(i.getMetadata().get(singleTag.getKey()))) {
                        return Boolean.FALSE;
                    }
                }
                return Boolean.TRUE;
            }).collect(Collectors.toList());
            return CollectionUtils.isEmpty(collect) ? pair.getSecond() : collect;
        }
        return pair.getSecond();
    }

    @Override
    public String name() {
        return "InstanceSelectByTag";
    }

    @Override
    public int order() {
        return 0;
    }
}
