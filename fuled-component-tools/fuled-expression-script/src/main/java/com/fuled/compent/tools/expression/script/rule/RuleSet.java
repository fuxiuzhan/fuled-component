package com.fuled.compent.tools.expression.script.rule;

import lombok.Data;

import java.util.List;

@Data
public class RuleSet {

    private String name;

    private List<Rule> rules;

    private String expression;

    private String value;
}
