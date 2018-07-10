package com.haoji.haoji.adapter;


import android.content.Context;

import com.baidu.mapapi.search.core.PoiInfo;
import com.haoji.haoji.R;
import com.haoji.haoji.adapter.base.BaseViewHolder;
import com.haoji.haoji.adapter.base.MyBaseAdapter;

import java.util.List;




public class NearAddressAdapter extends MyBaseAdapter<PoiInfo> {

	public NearAddressAdapter(Context context, int resource, List<PoiInfo> list) {
		super(context, resource, list);
	}

	@Override
	public void setConvert(BaseViewHolder viewHolder, PoiInfo info) {
		viewHolder.setTextView(R.id.item_address_name_tv, info.name);
		viewHolder.setTextView(R.id.item_address_detail_tv, info.address);
	}

}
