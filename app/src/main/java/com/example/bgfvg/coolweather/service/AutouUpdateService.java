package com.example.bgfvg.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.bgfvg.coolweather.receiver.AutoUpdateReceiver;
import com.example.bgfvg.coolweather.util.HttpCallbackListener;
import com.example.bgfvg.coolweather.util.HttpUtil;
import com.example.bgfvg.coolweather.util.Utility;

/**
 * Created by BGFVG on 2016/3/22.
 */
public class AutouUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        updateWeather();
                    }
                }
        ).start();

        //定时更新的
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 10*1000;
        long aTime = SystemClock.elapsedRealtime() + anHour;
        Intent i= new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME,aTime,pendingIntent);
        Log.e("itcast", "AutouUpdateService.onStartCommand.发送广播了");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     *更新Weather的方法
     */
    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);
        String weatherCode = prefs.getString("weather_code", "");
        String address = "http://www.weather.com.cn/data/cityinfo/" +
                weatherCode + ".html";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutouUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}
