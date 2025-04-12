package com.andyadc.kratos.exector.http;

import org.asynchttpclient.AsyncHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

import java.util.concurrent.CompletableFuture;

/**
 * 异步处理器
 */
public class AsyncHttpHelper {

    private AsyncHttpClient asyncHttpClient;

    private AsyncHttpHelper() {

    }

    public static AsyncHttpHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void initialized(AsyncHttpClient asyncHttpClient) {
        this.asyncHttpClient = asyncHttpClient;
    }

    public CompletableFuture<Response> executeRequest(Request request) {
        ListenableFuture<Response> future = asyncHttpClient.executeRequest(request);
        return future.toCompletableFuture();
    }

    public <T> CompletableFuture<T> executeRequest(Request request, AsyncHandler<T> handler) {
        ListenableFuture<T> future = asyncHttpClient.executeRequest(request, handler);
        return future.toCompletableFuture();
    }

    private static final class SingletonHolder {
        private static final AsyncHttpHelper INSTANCE = new AsyncHttpHelper();
    }

}
