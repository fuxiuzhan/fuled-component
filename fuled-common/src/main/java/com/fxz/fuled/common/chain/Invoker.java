package com.fxz.fuled.common.chain;

/**
 * @author fuled
 * @param <REQ>
 * @param <RES>
 */
public interface Invoker<REQ, RES> {

    /**
     * invoke next
     *
     * @param req
     * @return
     */
    RES invoke(REQ req);
}
