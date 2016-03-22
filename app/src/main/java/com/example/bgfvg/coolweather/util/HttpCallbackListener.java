package com.example.bgfvg.coolweather.util;

/**
 * Created by BGFVG on 2016/3/22.
 */
public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);
}
