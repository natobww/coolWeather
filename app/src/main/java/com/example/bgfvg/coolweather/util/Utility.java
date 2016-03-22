package com.example.bgfvg.coolweather.util;

import android.text.TextUtils;

import com.example.bgfvg.coolweather.db.CoolWeatherDB;
import com.example.bgfvg.coolweather.model.City;
import com.example.bgfvg.coolweather.model.County;
import com.example.bgfvg.coolweather.model.Province;

/**
 * Created by BGFVG on 2016/3/22.
 */
public class Utility {

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
}
