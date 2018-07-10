package com.haoji.haoji.comment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.custom.zxing.QRGenerateActivity;
import com.haoji.haoji.custom.zxing.QRScanActivity;
import com.haoji.haoji.model.Person;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.ui.RadarActivity;
import com.haoji.haoji.util.GetPhoneContect;
import com.haoji.haoji.util.LogUtils;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.ToastUtils;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class AddFriendActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack{

    @BindView(R.id.head_more)
    LinearLayout headMore;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.tv_my_qr)
    TextView tvMyQr;
    @BindView(R.id.ll_my_qr)
    LinearLayout llMyQr;
    @BindView(R.id.rl_leida)
    RelativeLayout rlLeida;
    @BindView(R.id.rl_saoyisao)
    RelativeLayout rlSaoyisao;
    @BindView(R.id.rl_contact)
    RelativeLayout rlContact;

    private JSONObject json;
    private String inputData,name,phones;
    private ProgressDialog progressDialog;
    private SharedPreferencesUtil util;
    /*
	 * 跳转联系人列表的回调函数
	 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (data == null) {
                    return;
                }
                // 处理返回的data,获取选择的联系人信息
                Uri uri = data.getData();
                String[] contacts = getContact(uri);
                name = contacts[0];
                phones = contacts[1];
                if (StringUtils.isEmpty(name) || StringUtils.isEmpty(phones)) {
                    ToastUtils.showShortToast(AddFriendActivity.this, "请把信息补充完整！");
                    return;
                }
                String phone=StringUtils.getCorrectPhone(phones);
                if (StringUtils.isPhone(phone)) {
                    etSearch.setText(phone);

                } else {
                    ToastUtils.showShortToast(AddFriendActivity.this, "您输入的号码有误！");
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    updateView();
                    break;

            }
        }
    };

    @Override
    public void initMainView() {
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);

    }

    @Override
    public void initUi() {
        Util.setHeadTitleMore(this, "添加朋友", true);
        headMore.setVisibility(View.GONE);
        util=new SharedPreferencesUtil(this);
        progressDialog = new ProgressDialog(this);
        tvMyQr.setText("我的好记号："+util.getMobilePhone());



    }
    public void updateView() {
        try {
            String phone=json.getString("phone");
           startActivity(new Intent(AddFriendActivity.this,UserDetailsActivity.class).putExtra("phone",phone));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadData() {

    }

    @OnClick({R.id.btn_send, R.id.ll_my_qr,R.id.rl_leida, R.id.rl_saoyisao,R.id.rl_contact})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                inputData=etSearch.getText().toString();
                if(StringUtils.isPhone(inputData)){
                    progressDialog.setMessage("正在查询...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();
                    NetIntent netIntent = new NetIntent();
                    netIntent.client_getUserByPhone(inputData, new NetIntentCallBackListener(this));
                }else {
                    ToastUtils.showShortToast(AddFriendActivity.this,"请输入11位手机号码！");
                }
                break;
            case R.id.ll_my_qr:
                startActivity(new Intent(AddFriendActivity.this, QRGenerateActivity.class).putExtra("info",util.getUserId()));
                break;
            case R.id.rl_leida:
                ArrayList<Person> list=new ArrayList<Person>();//创建一个保存获得的联系人的列表来存放数据
                  GetPhoneContect gpc=new GetPhoneContect();//创建一个获得联系人
                list= (ArrayList<Person>) gpc.GetPhoneContect(AddFriendActivity.this);
                for(int i=0;i<list.size();i++){
                    Person p=list.get(i);
                    String name=p.getName();
                    String phone=p.getPhone();

                    LogUtils.i("=======>"+"姓名："+name+"\n电话："+phone);
                }
                startActivity(new Intent(AddFriendActivity.this, RadarActivity.class));
                break;
            case R.id.rl_saoyisao:
                startActivity(new Intent(AddFriendActivity.this,QRScanActivity.class));
                break;
            case R.id.rl_contact:
                Uri uri = Uri.parse("content://contacts/people");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                startActivityForResult(intent, 0);
                break;

        }
    }

        /*
	 * 获取联系人姓名和电话号码
	 */
        private String[] getContact(Uri uri) {
            String[] contact = new String[2];
            // 得到ContentResolver对象
            ContentResolver cr = getContentResolver();
            // 取得电话本中开始一项的光标
            Cursor cursor = cr.query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                // 取得联系人姓名
                int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                contact[0] = cursor.getString(nameFieldColumnIndex);
                // 取得电话号码
                String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
                if (phone != null) {
                    phone.moveToFirst();
                    contact[1] = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                phone.close();
                cursor.close();
            } else {
                return null;
            }
            return contact;
        }



    @Override
    public void onError(Request request, Exception e) {
        if (progressDialog!=null){
            progressDialog.dismiss();
        }

    }

    @Override
    public void onResponse(String response) {
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("user")) {
                json = jsonObject.getJSONObject("user");
                handler.sendEmptyMessage(0);
            }else{
                ToastUtils.showShortToast(AddFriendActivity.this,"抱歉，该用户不存在！");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}
