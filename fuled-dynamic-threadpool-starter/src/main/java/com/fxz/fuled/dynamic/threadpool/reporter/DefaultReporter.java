package com.fxz.fuled.dynamic.threadpool.reporter;

import com.alibaba.fastjson.JSON;
import com.fxz.fuled.common.dynamic.threadpool.pojo.ReporterDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
public class DefaultReporter implements Reporter {
    @Override
    public void report(List<ReporterDto> records) {
        if (!CollectionUtils.isEmpty(records)) {
            records.forEach(r -> {
                log.info("reporter->{}", JSON.toJSONString(r));
            });
        }
    }
}
