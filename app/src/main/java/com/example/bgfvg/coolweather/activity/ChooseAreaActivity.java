package com.example.bgfvg.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bgfvg.coolweather.R;
import com.example.bgfvg.coolweather.db.CoolWeatherDB;
import com.example.bgfvg.coolweather.model.City;
import com.example.bgfvg.coolweather.model.County;
import com.example.bgfvg.coolweather.model.Province;
import com.example.bgfvg.coolweather.util.HttpCallbackListener;
import com.example.bgfvg.coolweather.util.HttpUtil;
import com.example.bgfvg.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BGFVG on 2016/3/22.
 */
public class ChooseAreaActivity extends Activity {

    //定义若干常量
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ListView listView;
    private TextView title;
    private List<String> dataList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB db;

    //省市县的集合
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    //当前选中的级别
    private int currentLevel;
    //选中的省市
    private Province selectProvince;
    private City selectCity;


    //进度提示框
    private ProgressDialog progressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        title = (TextView) findViewById(R.id.tv_title);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        if (adapter!=null) {
            listView.setAdapter(adapter);
        }
        //获取操作数据库的实力
        db = CoolWeatherDB.getInstance(getApplicationContext());
        //listView是设置条目点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel==LEVEL_PROVINCE){
                    selectProvince = provinceList.get(position);
                    queryCities();
                }else if (currentLevel ==LEVEL_CITY){
                    selectCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        //加载省级数据
        qureyProvinces();

    }

    /**
     * 优先从数据库查询查不到再服务器上面查
     */
    private void qureyProvinces() {
       provinceList = db.loadProvinces();
        if (provinceList.size()>0){
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            title.setText(getResources().getString(R.string.china));
            currentLevel = LEVEL_PROVINCE;
        }else{
            queryFromServer(null,getResources().getString(R.string.province));
        }
    }

    /**
     * 查询城市的信息
     */
    private void queryCities() {
        cityList = db.loadCities(selectProvince.getId());
        if (cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            title.setText(selectProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else{
            queryFromServer(selectProvince.getProvinceCode(),getResources().getString(R.string.city));
        }
    }

    /**
     * 查询线的信息
     */
    private void queryCounties() {
        countyList = db.loadCounties(selectCity.getId());
        if (countyList.size()>0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            title.setText(selectCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else{
            queryFromServer(selectCity.getCityCode(),getResources().getString(R.string.county));
        }
    }

    /**
     * 根据传入的code和type查询数据
     * @param code
     * @param type
     */
    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city"+code+".xml";

        }else{
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        //显示加载的进度条
        showProgressDiaolog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if (getResources().getString(R.string.province).equals(type)){
                    result = Utility.handleProvincesResponse(db,response);
                }else if(getResources().getString(R.string.city).equals(type)){
                    result = Utility.handleCitiesResponse(db,response,selectProvince.getId());
                }else if(getResources().getString(R.string.county).equals(type)){
                    result = Utility.handleCountiesResponse(db,
                            response, selectCity.getId());
                }
                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeDiaolog();
                            if (getResources().getString(R.string.province).equals(type)){
                                qureyProvinces();
                            }else if(getResources().getString(R.string.city).equals(type)){
                                queryCities();
                            }else if(getResources().getString(R.string.county).equals(type)){
                                queryCounties();
                            }

                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeDiaolog();
                        Toast.makeText(ChooseAreaActivity.this,getResources().getString(R.string.fail),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


    }

    private void showProgressDiaolog() {
        if (progressDialog==null){
            progressDialog = new ProgressDialog(ChooseAreaActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.fighting));
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();

    }

    /**
     * 显示加载进度条的方法
     */
    private void closeDiaolog() {
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel==LEVEL_CITY){
            qureyProvinces();
        }else{
            finish();
        }
    }
}
