package com.example.bgfvg.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.bgfvg.coolweather.service.AutouUpdateService;

/**
 * Created by BGFVG on 2016/3/22.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, AutouUpdateService.class);
        context.startService(intent1);
        Log.e("itcast", "AutoUpdateReceiver.onReceive."+"广播接收定时器发送的消息,又一次启动服务了");
    }
}
