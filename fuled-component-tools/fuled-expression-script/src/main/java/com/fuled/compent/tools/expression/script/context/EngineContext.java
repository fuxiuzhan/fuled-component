package com.fuled.compent.tools.expression.script.context;

import com.fuled.compent.tools.expression.script.rule.Rule;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class EngineContext {

    /**
     *
     */
    private String strategyCode;

    /**
     *
     */
    private String requestId;
    /**
     *
     */
    private Map<String, Object> params;

    /**
     *
     */
    private Map<String, Object> extra;

    /**
     *
     */
    private Map<String, Object> result;

    /**
     *
     */
    private List<Rule> hitRules;

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
}
