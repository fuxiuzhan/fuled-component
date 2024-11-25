package com.fuled.compent.tools.expression.script.strategy;

import com.fuled.compent.tools.expression.script.rule.Rule;
import com.fuled.compent.tools.expression.script.rule.RuleSet;
import lombok.Data;

import java.util.List;

@Data
public class Strategy {

    /**
     * 结果处理脚本
     */
    private String script;

    /**
     * 脚本类型
     */
    private String scriptType;

    /**
     * 规则集
     */

    private List<RuleSet> ruleSets;

    /**
     * 规则集表达式
     */
    private String expression;
}
