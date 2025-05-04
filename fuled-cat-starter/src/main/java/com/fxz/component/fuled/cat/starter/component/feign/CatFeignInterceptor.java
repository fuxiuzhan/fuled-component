package com.fxz.component.fuled.cat.starter.component.feign;

import com.dianping.cat.Cat;
import com.fxz.component.fuled.cat.starter.util.CatUtils;
import com.fxz.component.fuled.cat.starter.util.RequestAttributesUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
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
            requestTemplate.header(Cat.Context.ROOT, CatUtils.getRootId(requestAttributes));
            requestTemplate.header(Cat.Context.CHILD, CatUtils.getChildId(requestAttributes));
            requestTemplate.header(Cat.Context.PARENT, CatUtils.getParentId(requestAttributes));
            requestTemplate.header("application.name", Cat.getManager().getDomain());
        } else {
            String msgId = Cat.getCurrentMessageId();
            String parentId = Cat.getManager().getThreadLocalMessageTree().getParentMessageId();
            if (StringUtils.isEmpty(parentId)) {
                parentId = msgId;
            }
            String rootId = Cat.getManager().getThreadLocalMessageTree().getRootMessageId();
            if (StringUtils.isEmpty(rootId)) {
                rootId = msgId;
            }
            requestTemplate.header(Cat.Context.ROOT, rootId);
            requestTemplate.header(Cat.Context.CHILD, msgId);
            requestTemplate.header(Cat.Context.PARENT, parentId);
            requestTemplate.header("application.name", Cat.getManager().getDomain());
        }
    }
}
