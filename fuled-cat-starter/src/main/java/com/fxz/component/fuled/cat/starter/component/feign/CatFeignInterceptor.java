package com.fxz.component.fuled.cat.starter.component.feign;

import com.dianping.cat.Cat;
import com.fxz.component.fuled.cat.starter.util.CatUtils;
import com.fxz.component.fuled.cat.starter.util.RequestAttributesUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Objects;

/**
 * @author fxz
 */
public class CatFeignInterceptor implements RequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(CatFeignInterceptor.class);
    @Override
    public void apply(RequestTemplate requestTemplate) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(requestAttributes) && RequestAttributesUtil.isRequestActive(requestAttributes)) {
            requestTemplate.header("_catRootMessageId", new String[]{CatUtils.getRootId(requestAttributes)});
            requestTemplate.header("_catChildMessageId", new String[]{CatUtils.getChildId(requestAttributes)});
            requestTemplate.header("_catParentMessageId", new String[]{CatUtils.getParentId(requestAttributes)});
            requestTemplate.header("application.name", new String[]{Cat.getManager().getDomain()});
        }

    }
}
