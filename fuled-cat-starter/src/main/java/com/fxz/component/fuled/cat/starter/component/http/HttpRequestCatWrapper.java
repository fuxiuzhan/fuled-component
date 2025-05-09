package com.fxz.component.fuled.cat.starter.component.http;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.support.HttpRequestWrapper;

/**
 * @author fxz
 */
public class HttpRequestCatWrapper extends HttpRequestWrapper {
    private HttpHeaders httpHeaders;

    public HttpRequestCatWrapper(HttpRequest request) {
        super(request);
        this.httpHeaders = request.getHeaders();
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.httpHeaders;
    }
    public void addHeader(String headerName, String headerValue) {
        this.httpHeaders.add(headerName, headerValue);
    }
}
