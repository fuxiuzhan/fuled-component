package com.fuled.compent.tools.expression.script.source;

import lombok.Data;

@Data
public class SourceResult {
    /**
     *
     */
    private boolean success;
    /**
     *
     */

    private boolean hasError;
    /**
     *
     */

    private String errorMsg;

    /**
     * json 格式
     */
    private String data;
}
