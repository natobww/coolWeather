package com.example.bgfvg.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.bgfvg.coolweather.db.CoolWeatherDB;
import com.example.bgfvg.coolweather.model.City;
import com.example.bgfvg.coolweather.model.County;
import com.example.bgfvg.coolweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by BGFVG on 2016/3/22.
 */
public class Utility {

    /**
     * city : 宿州
     * cityid : 101220701
     * temp1 : 11℃
     * temp2 : 7℃
     * weather : 小雨转多云
     * img1 : d7.gif
     * img2 : n1.gif
     * ptime : 08:00
     */



    /**
     * 解析处理返回的省级数据
     * @param coolWeatherDB
     * @param response
     * @return
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response){

        if (!TextUtils.isEmpty(response)){
            String[] arrProvinces = response.split(",");
            if (arrProvinces!=null && arrProvinces.length>0){
                for(String p:arrProvinces){
                    String[] split = p.split("\\|");
                    Province newProvince = new Province();
                    newProvince.setProvinceCode(split[0]);
                    newProvince.setProvinceName(split[1]);
                    //将解析的Provinces保存在表里
                    coolWeatherDB.saveProvince(newProvince);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析City数据
     * @param coolWeatherDB
     * @param response
     * @param provinceId
     * @return
     */
    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] arrCities = response.split(",");
            if (arrCities!=null && arrCities.length>0){
                for(String p:arrCities){
                    String[] split = p.split("\\|");
                    City city = new City();
                    city.setCityCode(split[0]);
                    city.setCityName(split[1]);
                    city.setProvinceId(provinceId);
                    //解析的City保存在数据表中
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析County的数据
     * @param coolWeatherDB
     * @param response
     * @param cityId
     * @return
     */
    public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            String[] arrCounties = response.split(",");
            if (arrCounties!=null && arrCounties.length>0){
                for(String p:arrCounties){
                    String[] split = p.split("\\|");
                    County county = new County();
                    county.setCountyCode(split[0]);
                    county.setCountyName(split[1]);
                    county.setCityId(cityId);
                    //解析的数据保存在数据库表中
                    coolWeatherDB.saveCounty(county);

                }
                return true;
            }
        }
        return false;
    }

    /**
     * 处理天气信息
     * @param context
     * @param response
     */
    public static void handleWeatherResponse(Context context,String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherinfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherinfo.getString("city");
            String weatherCode = weatherinfo.getString("cityid");
            String weatherDesp = weatherinfo.getString("weather");
            String publishTime = weatherinfo.getString("ptime");
            String temp1 = weatherinfo.getString("temp1");
            String temp2 = weatherinfo.getString("temp2");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
                    weatherDesp, publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *将服务器返回的天气信息保存到sp中
     * @param context
     * @param cityName
     * @param weatherCode
     * @param temp1
     * @param temp2
     * @param weatherDesp
     * @param publishTime
     */
    private static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime) {
        //格式化字符串
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CANADA);
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();

    }


}
