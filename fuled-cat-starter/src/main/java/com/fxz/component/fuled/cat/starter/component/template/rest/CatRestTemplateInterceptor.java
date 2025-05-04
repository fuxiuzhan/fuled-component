package com.fxz.component.fuled.cat.starter.component.template.rest;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.fxz.component.fuled.cat.starter.component.http.HttpRequestCatWapper;
import com.fxz.component.fuled.cat.starter.util.CatUtils;
import com.fxz.component.fuled.cat.starter.util.RequestAttributesUtil;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

/**
 * @author fuled
 */
public class CatRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        URI uri = request.getURI();
        Transaction transaction = Cat.newTransaction("RemoteCall", uri.getPath());
        CatUtils.createConsumerCross(transaction, uri.getHost(), uri.getHost(), String.valueOf(uri.getPort()));
        HttpRequestCatWapper httpRequestCatWapper = new HttpRequestCatWapper(request);
        RequestAttributes requestAttributes = this.requestAttributesBuilder();
        if (Objects.nonNull(requestAttributes) && RequestAttributesUtil.isRequestActive(requestAttributes)) {
            httpRequestCatWapper.addHeader(Cat.Context.ROOT, CatUtils.getRootId(requestAttributes));
            httpRequestCatWapper.addHeader(Cat.Context.CHILD, CatUtils.getChildId(requestAttributes));
            httpRequestCatWapper.addHeader(Cat.Context.PARENT, CatUtils.getParentId(requestAttributes));
            httpRequestCatWapper.addHeader("application.name", Cat.getManager().getDomain());
            request = httpRequestCatWapper;
        }
        ClientHttpResponse response = null;
        try {
            response = execution.execute(request, body);
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            transaction.setStatus(e);
            throw e;
        } finally {
            transaction.complete();
        }
        return response;
    }

    private RequestAttributes requestAttributesBuilder() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(requestAttributes)) {
            return null;
        } else {
            String rootId = CatUtils.getRootId(requestAttributes);
            String childId = CatUtils.getChildId(requestAttributes);
            String parentId = CatUtils.getParentId(requestAttributes);
            if (StringUtils.isEmpty(rootId) && StringUtils.isEmpty(childId) && StringUtils.isEmpty(parentId)) {
                CatUtils.createMessageTree();
                return RequestContextHolder.getRequestAttributes();
            } else {
                return requestAttributes;
            }
        }
    }
}
