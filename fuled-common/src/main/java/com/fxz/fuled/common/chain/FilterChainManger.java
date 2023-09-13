package com.fxz.fuled.common.chain;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

/**
 * <p>
 * 流程详见上级接口说明
 *
 * @author fxz
 */
@Import(FilterConfig.class)
public class FilterChainManger implements FilterChain {

    @Autowired
    private FilterConfig filterConfig;

    @Override
    public Invoker buildInvokerChain(Invoker invoker, List<Filter> filters) {
        Invoker head = invoker;
        if (filters != null && filters.size() > 0) {
            for (int i = filters.size() - 1; i >= 0; i--) {
                Filter filter = filters.get(i);
                Invoker next = head;
                head = o -> filter.filter(o, next);
            }
        }
        return head;
    }

    /**
     * obtain head invoker
     *
     * @param group
     * @param invoker
     * @return
     */
    public Invoker getInvoker(String group, Invoker invoker) {
        List<Filter> filtersByGroup = filterConfig.getFiltersByGroup(group);
        return buildInvokerChain(invoker, filtersByGroup);
    }

}
