package com.fuled.compent.tools.expression.script.feature;

import com.fuled.compent.tools.expression.script.context.EngineContext;
import com.fuled.compent.tools.expression.script.rule.Rule;
import com.fuled.compent.tools.expression.script.rule.RuleSet;
import com.fuled.compent.tools.expression.script.strategy.Strategy;
import com.fuled.component.tools.dynamic.compile.loader.DynamicLoaderEngine;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.*;


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
        System.out.println(engineContext);
        /**Console output
         *
         ruleEval-------------code->ruleCode1
         dataSource--->ruleCode1
         evalJava result->strategyCode
         ruleEval-------------code->ruleCode2
         dataSource--->ruleCode2
         evalJava result->strategyCode
         value->true
         ((#ruleEval(#context,#ruleCode1) && #ruleEval(#context,#ruleCode2)) || (#ruleEval(#context,#ruleCode1) && #ruleEval(#context,#ruleCode2)) && (#ruleEval(#context,#ruleCode1) && #ruleEval(#context,#ruleCode2)) || (#ruleEval(#context,#ruleCode1) && #ruleEval(#context,#ruleCode2))) || ((#ruleEval(#context,#ruleCode1) && #ruleEval(#context,#ruleCode2)) || (#ruleEval(#context,#ruleCode1) && #ruleEval(#context,#ruleCode2)) && (#ruleEval(#context,#ruleCode1) && #ruleEval(#context,#ruleCode2)) || (#ruleEval(#context,#ruleCode1) && #ruleEval(#context,#ruleCode2)))
         true
         EngineContext(strategyCode=strategyCode, requestId=null, params={}, extra={rule_ruleCode1=100, rule_ruleCode2=100, evalJava_ruleCode2=strategyCode, evalJava_ruleCode1=strategyCode, source_ruleCode2=true, source_ruleCode1=true}, result={}, hitRuleSets=null, success=false, hasError=false, errorMsg=null)
         */
    }

    private static Map<String, Strategy> strategyCache = new HashMap<>();

    private static Map<String, Rule> ruleCache = new HashMap<>();

    private static Map<String, Feature> featureCache = new HashMap<>();


    public static void init() {
        Strategy strategy = new Strategy();
        strategy.setScript("");
        strategy.setExpression("($1 && $2) || ($1 && $2)");
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
        RuleSet ruleSet1 = new RuleSet();
        ruleSet1.setRules(rules);
        ruleSet1.setName("ruleSet1");
        ruleSet1.setExpression("$1 || $2");
        ruleSet1.setValue("ruleSet1Value");
        ruleSet1.setRules(rules);

        RuleSet ruleSet2 = new RuleSet();
        ruleSet2.setRules(rules);
        ruleSet2.setName("ruleSet2");
        ruleSet2.setExpression("$1 && $2");
        ruleSet2.setValue("ruleSet1Value");
        ruleSet2.setRules(rules);
        strategy.setRuleSets(Arrays.asList(ruleSet1, ruleSet2));

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

    private static String assembleEl(StandardEvaluationContext evalContext, EngineContext engineContext, String script, List<Rule> rules, String type) {
        //if circuit broker enabled ,if not expression every single variable
        String el = script;
        for (int i = rules.size(); i > 0; i--) {
            evalContext.setVariable("ruleCode" + i, rules.get(i - 1).getRuleCode());
            el = el.replace("$" + i + "", "#ruleEval(#context,#ruleCode" + i + ")");
        }
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
        StandardEvaluationContext evalContext = getEvalContext();
        evalContext.setVariable("context", engineContext);
        String el = strategy.getExpression();
        for (int i = strategy.getRuleSets().size(); i > 0; i--) {
            el = el.replace("$" + i, assembleEl(evalContext, engineContext, strategy.getExpression(), strategy.getRuleSets().get(i - 1).getRules(), "rule"));
        }
        ExpressionParser parser = new SpelExpressionParser();
        Boolean value = parser.parseExpression(el).getValue(evalContext, Boolean.class);
        System.out.println("value->" + value);
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
        engineContext.getExtra().put("rule_" + ruleCode, o);
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
        String s = evalJava(engineContext, dataSource, "return  context.getStrategyCode();");
        System.out.println("evalJava result->" + s);
        engineContext.getExtra().put("source_" + dataSource, Boolean.TRUE);
        return "100";
    }


    public static String evalJava(EngineContext engineContext, String dataSource, String script) {
        String BASE_JAVA = "package com.fuled.compent.tools.expression.script.feature;" +
                "import com.fuled.compent.tools.expression.script.context.EngineContext;" +
                "public class %s {" +
                "    public String invoke(EngineContext context) {" +
                "        %s" +
                "    }" +
                "}";
        String javaSource = String.format(BASE_JAVA, dataSource, script);
        byte[] clazzBytes = DynamicLoaderEngine.compile(javaSource);
        //分部署存储可直接存储class字节码，使用时直接执行，免去编译
        Class<?> aClass = DynamicLoaderEngine.loadClass(clazzBytes);
        try {
            Object o = aClass.newInstance();
            //instance cache
            Method invoke = o.getClass().getDeclaredMethod("invoke", EngineContext.class);
            Object invoke1 = invoke.invoke(o, engineContext);
            engineContext.getExtra().put("evalJava_" + dataSource, invoke1);
            return (String) invoke1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
