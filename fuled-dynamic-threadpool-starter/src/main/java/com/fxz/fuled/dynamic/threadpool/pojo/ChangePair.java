package com.fxz.fuled.dynamic.threadpool.pojo;

import com.fxz.fuled.dynamic.threadpool.enums.ThreadPoolItemsEnum;
import lombok.Data;

@Data
public class ChangePair {
    private Object value;
    private ThreadPoolItemsEnum type;
}
