package com.fxz.fuled.threadpool.monitor.pojo;

import com.fxz.fuled.threadpool.monitor.enums.ThreadPoolItemsEnum;
import lombok.Data;

@Data
public class ChangePair {
    private Object value;
    private ThreadPoolItemsEnum type;
}
