package com.haoji.haoji.comment;

import android.content.Intent;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.ui.AddEnergyActivity;
import com.haoji.haoji.ui.MoreByYearActivity;
import com.haoji.haoji.util.SPUtils;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.TimeUtils;
import com.haoji.haoji.util.ToastUtils;
import com.squareup.okhttp.Request;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class SocialMainActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack{

	private PullToRefreshListView pull_refresh_list;
	private List<JSONObject> articles=new ArrayList<JSONObject>();

	private SocialMainAdapter adapter;
	private ListView actualListView;
	private int page = 1;
	private SharedPreferencesUtil util;
	private ImageView iv_back;

	String userID,infoNum;
	List<String> sIDs = new ArrayList<String>();

	@Override
	public void initMainView() {
		setContentView(R.layout.activity_social_main);
	}
	@Override
	public void initUi() {
		userID = this.getIntent().getStringExtra("userid");
		util=new SharedPreferencesUtil(this);
		initView();}

	@Override
	public void loadData() {

		new NetIntent().client_getMyUnreadComminfo(util.getUserId(),  "1", new NetIntentCallBackListener(SocialMainActivity.this));
		File dir = new File("/sdcard/haoji");
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}


	private void initView() {
		pull_refresh_list = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		pull_refresh_list.setMode(Mode.BOTH);
		pull_refresh_list
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(
								SocialMainActivity.this,
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);


						if (pull_refresh_list.getCurrentMode() == Mode.PULL_FROM_START) {
							page = 1;

						} else if (pull_refresh_list.getCurrentMode() == Mode.PULL_FROM_END) {
							page++;
						}
						getData(page);//TODO  刷新获取更多数据
					}
				});

		actualListView = pull_refresh_list.getRefreshableView();
		adapter = new SocialMainAdapter(SocialMainActivity.this, articles);
		actualListView.setAdapter(adapter);
		actualListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				adapter.hideCommentEditText();
				return false;
			}

		});
		getData(1);
		pull_refresh_list.setRefreshing(false);
		ImageView iv_camera = (ImageView) this.findViewById(R.id.iv_camera);
		iv_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(SocialMainActivity.this, AddEnergyActivity.class).putExtra("value",util.getUserId()),1);
			}

		});
		ImageView iv_search = (ImageView) this.findViewById(R.id.iv_search);
		iv_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SocialMainActivity.this, MoreByYearActivity.class));
			}
		});
		iv_back = (ImageView) this.findViewById(R.id.iv_back);
		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}
	private void getData(final int page_num) {           //TODO 加载数据逻辑
		NetIntent netIntent = new NetIntent();
		netIntent.client_getFriendEnergyList(util.getUserId(), page_num + "", new NetIntentCallBackListener(this));
		page=page_num;
	}

	@Override
	public void onResume() {
		super.onResume();
		getData(1);
	}

	@Override
	public void onError(Request request, Exception e) {
		ToastUtils.showShortToast(SocialMainActivity.this,"服务器繁忙,请稍后!");
	}

	@Override
	public void onResponse(String response) {
		JSONObject jsonObject = null;
		try {
			jsonObject=JSON.parseObject(response);
			//LogUtils.i("返回数据：====》"+jsonObject.toString());//TODO 打印转换过来的效果
		//String code = jsonObject.getString("code");
		if (jsonObject.containsKey("energyList")) {
			pull_refresh_list.onRefreshComplete();
			JSONArray users_temp = jsonObject.getJSONArray("energyList");
			util.setTime(TimeUtils.getNowTime());//TODO  记录上次刷新时间
			if (page == 0) {
				articles.clear();//如果是首页，先把集合清理干净
				sIDs.clear();
				for (int i = 0; i < users_temp.size(); i++) {
					JSONObject json = users_temp.getJSONObject(i);
					String sID = json.getString("id");
					sIDs.add(sID);
					articles.add(json);
				}
			} else {
				for (int i = 0; i < users_temp.size(); i++) {
					JSONObject json = users_temp.getJSONObject(i);
					String sID = json.getString("id");
					if (!sIDs.contains(sID)) {
						sIDs.add(sID);
						articles.add(json);
					}
				}

			}

			//adapter.notifyDataSetChanged();
			//SPUtils.put(SocialMainActivity.this,"infoCnt","9");
		}  else if(jsonObject.containsKey("infoCnt")){
			infoNum=jsonObject.getString("infoCnt");
			//LogUtils.i("infoNum=========>:"+infoNum);
			if(StringUtils.isNotEmpty(infoNum,true)){
				SPUtils.put(SocialMainActivity.this,"infoCnt",infoNum);
				//LogUtils.i("infoNum2=========>:"+infoNum);
			}
		}else {
			//ToastUtils.showShortToast(SocialMainActivity.this,"亲，已经见底啦!");
		}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
