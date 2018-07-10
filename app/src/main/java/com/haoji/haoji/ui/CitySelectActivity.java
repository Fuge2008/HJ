package com.haoji.haoji.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.custom.CustomProgress;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;




public class CitySelectActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack {
    private static String TAG = CitySelectActivity.class.getSimpleName();

    @BindView(R.id.listview_province)
    ListView listviewProvince;
    @BindView(R.id.listview_city)
    ListView listviewCity;


    private ArrayList<HashMap> proMaps = new ArrayList<HashMap>();
    private ArrayList<HashMap> cityMaps = new ArrayList<HashMap>();
    private ProvinceAdapter provinceAdapter;
    private CityAdapter cityAdapter;
    //private String universityid = "";
    private int type;

    private String strProvince;
    private String strCity;
    private CustomProgress dialog;
    //public static final String DATA_STR = "DATA_STR_CODE";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void initMainView() {
        setContentView(R.layout.activity_city_select);
        ButterKnife.bind(this);
        //universityid = this.getIntent().getStringExtra("universityid");
        //type = this.getIntent().getIntExtra("type", 0);
        Util.setHeadTitleMore(this,"城市选择",true);
        findViewById(R.id.head_more).setVisibility(View.GONE);
      
        provinceAdapter = new ProvinceAdapter();
        cityAdapter = new CityAdapter();
        listviewProvince.setAdapter(provinceAdapter);
        listviewCity.setAdapter(cityAdapter);
        listviewProvince.setOnItemClickListener(onProvinceItemClickListener);
        listviewCity.setOnItemClickListener(onCityItemClickListener);
        //发送请求
        dialog=CustomProgress.show(this,"加载中..",true,null);
        new NetIntent().client_getProvinceList(new NetIntentCallBackListener(this));

    }

    @Override
    public void initUi() {

    }

    @Override
    public void loadData() {

    }

    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    provinceAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    cityAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    private AdapterView.OnItemClickListener onProvinceItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap map=proMaps.get(position);
            strProvince=proMaps.get(position).get("province").toString();
            dialog=CustomProgress.show(CitySelectActivity.this,"加载中..",true,null);
            new NetIntent().client_getCityList(map.get("province").toString(),new NetIntentCallBackListener(CitySelectActivity.this));
        }
    };

    private AdapterView.OnItemClickListener onCityItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap map=cityMaps.get(position);
            strCity=cityMaps.get(position).get("city").toString();
            Intent intent = new Intent(CitySelectActivity.this, ProfileActivity.class);
            intent.putExtra("province", strProvince);
            intent.putExtra("city", strCity);
            setResult(RESULT_OK, intent);
            finish();// 结束之后会将结果传回

        }
    };

    @Override
    public void onError(Request request, Exception e) {

    }

    @Override
    public void onResponse(String response) {
        if (dialog!=null)
            dialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("provinceList")) {
                proMaps.clear();
                proMaps.addAll(Util.toHashMap(new JSONArray(jsonObject.getString("provinceList"))));
                handler.sendEmptyMessage(0);
            }
            if (jsonObject.has("cityList")) {
                cityMaps.clear();
                cityMaps.addAll(Util.toHashMap(new JSONArray(jsonObject.getString("cityList"))));
                handler.sendEmptyMessage(1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




    class ProvinceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return proMaps.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(CitySelectActivity.this).inflate(R.layout.listview_item_text, null);
            TextView item_text = (TextView) convertView.findViewById(R.id.item_text);
            HashMap map = proMaps.get(position);
            item_text.setText(map.get("province").toString());
            return convertView;
        }
    }

    class CityAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cityMaps.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(CitySelectActivity.this).inflate(R.layout.listview_item_text, null);
            TextView item_text = (TextView) convertView.findViewById(R.id.item_text);
            HashMap map = cityMaps.get(position);
            item_text.setText(map.get("city").toString());
            return convertView;
        }
    }
}
