package com.mahta.rastin.broadcastapplication.helper;


import android.content.ContentValues;

import com.mahta.rastin.broadcastapplication.global.G;
import com.mahta.rastin.broadcastapplication.interfaces.OnResultListener;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpManager{

    private OnResultListener onResultListener;
    private OkHttpClient client;
    private String httpResult;
    private static final int CONNECT_TIMEOUT = 10;
    private static final int WRITE_TIMEOUT = 10;
    private static final int READ_TIMEOUT = 30;

    HttpManager(){
        client = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT,TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    public void post(String url, ContentValues params){
        FormBody.Builder builder = new FormBody.Builder();
        if (params!=null && params.size() > 0)
            for (String key:params.keySet()) {
                builder.add(key,params.getAsString(key));
            }

        RequestBody body = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        doRequest(request);
    }

    public void get(String url,String[] args){
        StringBuilder builder = new StringBuilder();
        builder.append(url);
        if (args != null && args.length>0)
            for (String arg:args) {
                builder.append("/");
                builder.append(arg);
            }

//        try {
//            URLEncoder.encode(builder.toString(), "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        G.i(builder.toString()); // TODO: 6/3/18 remove this line
        Request request = new Request.Builder()
                .url(builder.toString())
                .get()
                .build();

        doRequest(request);
    }

    private void doRequest(final Request request){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Response response;
                try {
                    response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        G.e(response.body().string());
                    }else {
                        httpResult = response.body().string();
                        if(onResultListener!=null){
                            G.HANDLER.post(new Runnable() {
                                @Override
                                public void run() {
                                    onResultListener.onResult(httpResult);
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    G.e(e.getMessage());
                }
            }
        });
        thread.start();

    }

    public void setOnResultListener(OnResultListener onResultListener){
        this.onResultListener = onResultListener;
    }

}
