package com.fxz.fuled.common.chain;

import java.util.List;

/**
 * <p>
 * 过滤链构建接口
 * <p>
 * 流程为  importor.handle->merticRunner.handle->preHeadInvoker.invoke->reader.read->postHeadInvoker.invoke->exporter.exoprt->storageHeadInvoker->exporter.doExport
 * @author fxz
 */
public interface FilterChain<T> {
    /**
     * 构建处理链，并返回head Invoker
     *
     * @param invoker
     * @param filters
     * @return
     */

    Invoker buildInvokerChain(Invoker invoker, List<Filter> filters);


}
