package io.github.kimmking.gateway.outbound.okhttp;


import io.github.kimmking.gateway.filter.HeaderHttpResponseFilter;
import io.github.kimmking.gateway.filter.HttpRequestFilter;
import io.github.kimmking.gateway.filter.HttpResponseFilter;
import io.github.kimmking.gateway.outbound.httpclient4.NamedThreadFactory;
import io.github.kimmking.gateway.router.HttpEndpointRouter;
import io.github.kimmking.gateway.router.WeightHttpEndpointRouter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * 转发后台
 * @author ahuxh
 */
public class OkhttpOutboundHandler {
    //
    private OkHttpClient httpclient;
    // 换一个 HTTP实现
    private ExecutorService proxyService;
    private List<String> backendUrls;

    // 过滤器 (试试责任链??)
    HttpResponseFilter filter = new HeaderHttpResponseFilter();
    // 随机分发 算法
//  HttpEndpointRouter router = new RandomHttpEndpointRouter();
    // 轮询分发 算法
//  HttpEndpointRouter router = new RoundRibbonHttpRounter();
    // 平滑加权轮询 算法
    HttpEndpointRouter router= new WeightHttpEndpointRouter();


    public OkhttpOutboundHandler(List<String> backends) {

        this.backendUrls = backends.stream().map(this::formatUrl).collect(Collectors.toList());
        // 线程池配置
        int cores = Runtime.getRuntime().availableProcessors();
        long keepAliveTime = 1000;
        int queueSize = 2048;
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();//.DiscardPolicy();
        proxyService = new ThreadPoolExecutor(cores, cores,
                keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize),
                new NamedThreadFactory("proxyService"), handler);
        // 启动客户端
        httpclient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)//连接超时时间
                .writeTimeout(10, TimeUnit.SECONDS)//写入超时时间
                .readTimeout(500, TimeUnit.MILLISECONDS)//读取超时时间
                .build();
    }


    public void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, HttpRequestFilter filter) {
        // 路由~~
        String backendUrl = router.route(this.backendUrls);
        final String url = backendUrl + fullRequest.uri();
        filter.filter(fullRequest, ctx);
        proxyService.submit(()->fetchGet(url, ctx));
    }

    private void fetchGet(String url, final ChannelHandlerContext ctx) {

        Request request = new Request.Builder()
                .url(url)
                .build();

        httpclient.newCall(request).enqueue(new Callback() {//异步请求
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);
                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    System.out.println("【" + responseHeaders.name(i) + "】" + responseHeaders.value(i));
                }
                String res = response.body().string();
                System.out.println("【响应结果】" + res);

                FullHttpResponse httpResponse = null;
                httpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(res.getBytes()));
                httpResponse.headers().set("Content-Type", "application/json");
                httpResponse.headers().setInt("Content-Length", Integer.parseInt(responseHeaders.get("Content-Length")));
                filter.filter(httpResponse);
                ctx.write(httpResponse);
                ctx.flush();

            }
        });
    }


    // 格式化 url
    private String formatUrl(String backend) {
        return backend.endsWith("/")?backend.substring(0,backend.length()-1):backend;
    }
}
