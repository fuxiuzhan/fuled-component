package com.fuled.compent.tools.expression.script.rule;


import lombok.Data;

@Data
public class Rule {

    private String ruleCode;

    private String ruleName;

    private String featureCode;

    private String operator;

    private String rightValue;
}
