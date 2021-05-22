package com.ahuxh.client;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class OKHttpClient {


    public static void main(String[] args) throws IOException {
        String url = "http://localhost:8801";
        String res = getAsString(url);
        System.out.println("res:" + res);
        // 将静态 资源置为 null,方便垃圾回收
        client = null;
    }

    // 初始化一个 静态的 客户端实例
    public static OkHttpClient client = new OkHttpClient();

    // get请求
    public static String getAsString(String url) throws IOException {
        // 构造请求
        Request request = new Request.Builder()
                .url(url)
                .build();
        //调用
        try (Response  response = client.newCall(request).execute()) {
//            return response.message();
            return response.body().string();
        }
    }

}
