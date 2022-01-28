package com.fxz.fuled.common.chain;


import java.util.List;

/**
 * <p>
 * 流程详见上级接口说明
 *
 * @author fxz
 */
public class FilterChainManger implements FilterChain {
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
}
