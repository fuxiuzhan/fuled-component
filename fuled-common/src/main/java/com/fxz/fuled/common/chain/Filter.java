package com.fxz.fuled.common.chain;

/**
 * 过滤器
 *
 * @param <REQ>
 * @param <RES>
 * @author fuled
 */
public interface Filter<REQ, RES> {

    /**
     * filter context
     *
     * @param req
     * @param invoker
     * @return
     */
    RES filter(REQ req, Invoker<REQ, RES> invoker);
}
