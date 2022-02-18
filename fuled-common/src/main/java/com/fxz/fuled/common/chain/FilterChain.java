package com.fxz.fuled.common.chain;

import java.util.List;

/**
 * <p>
 * 过滤链构建接口
 * <p>
 * example:  importer.handle->metricRunner.handle->preInvoker.invoke->reader.read->postInvoker.invoke->exporter.export->storageHeadInvoker->exporter.doExport
 *
 * @author fxz
 */
public interface FilterChain {
    /**
     * 构建处理链，并返回head Invoker
     *
     * @param invoker
     * @param filters
     * @return
     */

    Invoker buildInvokerChain(Invoker invoker, List<Filter> filters);

}
