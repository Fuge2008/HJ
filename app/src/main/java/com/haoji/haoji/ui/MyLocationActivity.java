package com.haoji.haoji.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.haoji.haoji.R;
import com.haoji.haoji.adapter.NearAddressAdapter;
import com.haoji.haoji.adapter.SearchAddressAdapter;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.custom.ClearEditText;
import com.haoji.haoji.util.LogUtils;
import com.haoji.haoji.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MyLocationActivity extends BaseActivity implements OnGetPoiSearchResultListener, BDLocationListener {
    private static String TAG = MyLocationActivity.class.getSimpleName();
    @BindView(R.id.ce_input_location)
    ClearEditText ceInputLocation;
    @BindView(R.id.lv_near_location)
    ListView lvNearLocation;
    @BindView(R.id.lv_search_location)
    ListView lvSearchLocation;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;


    public LocationClient mLocationClient = null;


    private PoiSearch mPoiSearch = null;
    private String cityName = "深圳";
    private LatLng start_Latlng = new LatLng(22.6650909715, 114.0693295678);
    private NearAddressAdapter nearAddressAdapter = null;
    private SearchAddressAdapter searchAddressAdapter = null;
    private List<PoiInfo> nearAddresses = new ArrayList<PoiInfo>();
    private List<PoiInfo> searchAddresses = new ArrayList<PoiInfo>();
    public static final String DATA_STR = "DATA_STR_CODE";
    private boolean isSearch = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void initMainView() {
        setContentView(R.layout.activity_my_location);
        ButterKnife.bind(this);
        Util.setHeadTitleMore(this, "所在位置", true);
        findViewById(R.id.head_more).setVisibility(View.GONE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//禁止键盘弹出

    }

    @Override
    public void initUi() {
        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        //定位
        mLocationClient = new LocationClient(getApplicationContext());//声明LocationClient类
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向

        mLocationClient.setLocOption(option);
        mLocationClient.start();
        if (mLocationClient != null && mLocationClient.isStarted())
            mLocationClient.requestLocation();
        //注册监听函数
        mLocationClient.registerLocationListener(this);

        mPoiSearch.searchNearby(
                new PoiNearbySearchOption().keyword("酒店").pageCapacity(20).radius(5000).location(start_Latlng));

    }

    @Override
    public void loadData() {
        ceInputLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPoiSearch.searchInCity((new PoiCitySearchOption()).city(cityName).keyword(s.toString()).pageNum(0)
                        .pageCapacity(20));
                if (searchAddresses != null) {
                    tvCancel.setVisibility(View.VISIBLE);
                    lvNearLocation.setVisibility(View.GONE);
                    lvSearchLocation.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        nearAddressAdapter = new NearAddressAdapter(this, R.layout.item_near_address, nearAddresses);
        lvNearLocation.setAdapter(nearAddressAdapter);
        lvNearLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PoiInfo poiInfo = nearAddresses.get(position);
                //TODO 设置详细地址
                LogUtils.i("+++++++++++++++>" + nearAddresses.get(0).address.toLowerCase());
                Intent intent = new Intent(MyLocationActivity.this, AddEnergyActivity.class);
                intent.putExtra(DATA_STR, poiInfo.name);
                setResult(RESULT_OK, intent);
                finish();// 结束之后会将结果传回

            }
        });
        searchAddressAdapter = new SearchAddressAdapter(this, R.layout.item_search_address, searchAddresses);
        lvSearchLocation.setAdapter(searchAddressAdapter);
        lvSearchLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PoiInfo poiInfo = searchAddresses.get(position);

                LogUtils.i("=====>" + searchAddresses.get(0).address.toLowerCase());
                Intent intent = new Intent(MyLocationActivity.this, AddEnergyActivity.class);
                intent.putExtra(DATA_STR, poiInfo.name);
                setResult(RESULT_OK, intent);
                finish();// 结束之后会将结果传回

            }
        });

    }

    @Override
    protected void onDestroy() {
        mPoiSearch.destroy();
        super.onDestroy();

    }

    @OnClick(R.id.tv_cancel)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation == null) {
            return;
        }
        double Latitude = bdLocation.getLatitude(); // 获取经度
        double Longitude = bdLocation.getLongitude(); // 获取纬度
        String City = bdLocation.getCity();
        String CityID = bdLocation.getCityCode();
        String time = bdLocation.getTime();
        String address = bdLocation.getAddrStr();
        if (bdLocation.getCity() != null) {
            cityName = bdLocation.getCity();
        }
        double start_longitude = bdLocation.getLongitude();
        double start_latitude = bdLocation.getLatitude();
        LatLng start_Latlng1 = new LatLng(start_latitude, start_longitude);
        if (start_Latlng1 != null) {
            start_Latlng = start_Latlng1;
        }

    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult arg0) {
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
    }


    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            List<PoiInfo> list = result.getAllPoi();
            if (list != null && list.size() > 0) {
                searchAddresses.clear();
                searchAddresses.addAll(list);
                searchAddressAdapter.notifyDataSetChanged();
            }
            if (list != null && list.size() > 0) {
                nearAddresses.clear();
                nearAddresses.addAll(list);
                nearAddressAdapter.notifyDataSetChanged();
            }
        }
    }

}
