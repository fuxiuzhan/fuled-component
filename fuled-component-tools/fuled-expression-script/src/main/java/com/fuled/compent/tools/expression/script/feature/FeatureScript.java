package com.fuled.compent.tools.expression.script.feature;

import com.fuled.compent.tools.expression.script.context.EngineContext;
import com.fuled.compent.tools.expression.script.rule.Rule;
import com.fuled.compent.tools.expression.script.strategy.Strategy;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 */
public class FeatureScript {


    /**
     * @param args
     */
    public static void main(String[] args) {
        init();
        EngineContext engineContext = new EngineContext();
        engineContext.setStrategyCode("strategyCode");
        System.out.println(execute(engineContext));
    }

    private static Map<String, Strategy> strategyCache = new HashMap<>();

    private static Map<String, Rule> ruleCache = new HashMap<>();

    private static Map<String, Feature> featureCache = new HashMap<>();


    public static void init() {
        Strategy strategy = new Strategy();
        strategy.setScript("");
        strategy.setExpression("$2 || ($1 && $2)");
        strategy.setScriptType("java");
        List<Rule> rules = new ArrayList<>();
        Rule rule1 = new Rule();
        rule1.setRuleCode("ruleCode1");
        rule1.setFeatureCode("featureCode1");
        Feature feature1 = new Feature();
        feature1.setCode("featureCode1");
        feature1.setFiled("filed1");
        feature1.setSourceCode("source1");
        feature1.setScript("filed1");
        feature1.setScriptType("java");
        featureCache.put(feature1.getCode(), feature1);
        rule1.setOperator(">");
        rule1.setRightValue("50");
        ruleCache.put(rule1.getRuleCode(), rule1);
        rules.add(rule1);
        Rule rule2 = new Rule();
        rule2.setRuleCode("ruleCode2");
        rule2.setFeatureCode("featureCode2");
        Feature feature2 = new Feature();
        feature2.setCode("featureCode2");
        feature2.setFiled("filed1");
        feature2.setSourceCode("source1");
        feature2.setScript("filed1");
        feature2.setScriptType("java");
        featureCache.put(feature2.getCode(), feature2);
        rule2.setOperator(">");
        rule2.setRightValue("20");
        ruleCache.put(rule2.getRuleCode(), rule2);
        rules.add(rule2);
        strategy.setRules(rules);
        strategyCache.put("strategyCode", strategy);
    }


    /**
     * @return
     */
    public static StandardEvaluationContext getEvalContext() {
        try {
            StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();
            standardEvaluationContext.registerFunction("ruleEval", FeatureScript.class.getMethod("ruleEval", EngineContext.class, String.class));
            return standardEvaluationContext;
        } catch (Exception e) {
        }
        return null;
    }

    private static String assembleEl(EngineContext engineContext, String script, List<Rule> rules, String type) {
        StandardEvaluationContext evalContext = getEvalContext();
        evalContext.setVariable("context", engineContext);
        //if circuit broker enabled ,if not expression every single variable
        String el = script;
        for (int i = rules.size(); i > 0; i--) {
            evalContext.setVariable("ruleCode" + i, rules.get(i - 1).getRuleCode());
            el = el.replace("$" + i + "", "#ruleEval(#context,#ruleCode" + i + ")");
        }
        ExpressionParser parser = new SpelExpressionParser();
        Boolean value = parser.parseExpression(el).getValue(evalContext, Boolean.class);
        System.out.println("value->" + value);
        return el;
    }

    /**
     * 1 && 2
     *
     * @param engineContext
     * @return
     */
    public static boolean execute(EngineContext engineContext) {
        Strategy strategy = strategyCache.get(engineContext.getStrategyCode());
        //1 && 2
        String el = assembleEl(engineContext, strategy.getExpression(), strategy.getRules(), "rule");
        System.out.println(el);
        return Boolean.TRUE;
    }


    /**
     * 1 && 2=> $rule1 && $rule2
     *
     * @param engineContext
     * @param ruleCode
     * @return
     */
    public static boolean ruleEval(EngineContext engineContext, String ruleCode) {
        System.out.println("ruleEval-------------code->" + ruleCode);
        String o = fetchFormSource(engineContext, ruleCode);
        //compare utils

        //datasource
        return Boolean.TRUE;
    }


    /**
     * @param engineContext
     * @param dataSource
     * @return
     */
    public static String fetchFormSource(EngineContext engineContext, String dataSource) {
        //get source from type and execute request
        System.out.println("dataSource--->" + dataSource);
        return "100";
    }


}
