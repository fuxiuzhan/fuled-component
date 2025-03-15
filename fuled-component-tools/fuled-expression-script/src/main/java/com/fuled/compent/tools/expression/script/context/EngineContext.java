package com.fuled.compent.tools.expression.script.context;

import com.fuled.compent.tools.expression.script.rule.RuleSet;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private Map<String, Object> params = new ConcurrentHashMap<>();

    /**
     *
     */
    private Map<String, Object> extra = new ConcurrentHashMap<>();

    /**
     *
     */
    private Map<String, Object> result = new ConcurrentHashMap<>();

    /**
     *
     */
    private List<RuleSet> hitRuleSets;

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
