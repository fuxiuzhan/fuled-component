package com.fxz.component.fuled.cat.starter.component.feign;

import com.dianping.cat.Cat;
import com.fxz.component.fuled.cat.starter.util.CatTraceCarrier;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fxz
 */
public class CatFeignInterceptor implements RequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(CatFeignInterceptor.class);

    @Override
    public void apply(RequestTemplate requestTemplate) {
        CatTraceCarrier.Context context = CatTraceCarrier.getContext();
        requestTemplate.header(Cat.Context.ROOT, context.getRootTrace());
        requestTemplate.header(Cat.Context.CHILD, context.getChildTrace());
        requestTemplate.header(Cat.Context.PARENT, context.getParentTrace());
        requestTemplate.header("application.name", Cat.getManager().getDomain());
    }
}
