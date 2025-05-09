package com.fxz.component.fuled.cat.starter.component.template.rest;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.fxz.component.fuled.cat.starter.component.http.HttpRequestCatWrapper;
import com.fxz.component.fuled.cat.starter.util.CatTraceCarrier;
import com.fxz.component.fuled.cat.starter.util.CatUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;

/**
 * @author fuled
 */
public class CatRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        URI uri = request.getURI();
        Transaction transaction = Cat.newTransaction("RemoteCall", uri.getPath());
        CatUtils.createConsumerCross(transaction, uri.getHost(), uri.getHost(), String.valueOf(uri.getPort()));
        HttpRequestCatWrapper httpRequestCatWrapper = new HttpRequestCatWrapper(request);
        requestAttributesBuilder();
        CatTraceCarrier.Context context = CatTraceCarrier.getContext();
        httpRequestCatWrapper.addHeader(Cat.Context.ROOT, context.getRootTrace());
        httpRequestCatWrapper.addHeader(Cat.Context.CHILD, context.getChildTrace());
        httpRequestCatWrapper.addHeader(Cat.Context.PARENT, context.getParentTrace());
        httpRequestCatWrapper.addHeader("application.name", Cat.getManager().getDomain());
        request = httpRequestCatWrapper;
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

    private void requestAttributesBuilder() {
        CatTraceCarrier.Context context = CatTraceCarrier.getContext();
        String rootId = context.getRootTrace();
        String childId = context.getChildTrace();
        String parentId = context.getParentTrace();
        if (StringUtils.isEmpty(rootId) && StringUtils.isEmpty(childId) && StringUtils.isEmpty(parentId)) {
            CatUtils.createMessageTree();
        }
    }
}
