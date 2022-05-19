package com.summon.finder.service;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HTTPTask extends AsyncTask<String, String, String> {
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(10,TimeUnit.SECONDS)
            .readTimeout(15,TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();

    public HTTPTask() {
    }

    @Override
    protected String doInBackground(String... strings) {
        Request.Builder builder = new Request.Builder();
        builder.url(strings[0]);

        Request request = builder.build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if(!s.equals("")){

        }

        super.onPostExecute(s);
    }
}
