//package com.haoji.haoji.ui;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.widget.Toast;
//
//import com.haoji.haoji.R;
//import com.haoji.haoji.base.BaseActivity;
//import com.haoji.haoji.model.TabClass;
//
//import java.util.ArrayList;
//
//
//
//
//public class ClassTableFragmentActivity extends BaseActivity {
//    private static String TAG = ClassTableFragmentActivity.class.getSimpleName();
//
//    private TableClass tableCanvas;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//    }
//
//    private TableClass.OnClickTableListener onClickTableListener = new TableClass.OnClickTableListener() {
//
//        @Override
//        public void ClickItem(int row, int col) {
//        }
//
//        @Override
//        public void ClickDoubleItem(int row, int col) {
//            Toast.makeText(ClassTableFragmentActivity.this, row + " " + col, Toast.LENGTH_SHORT).show();
//        }
//    };
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    public void initMainView() {
//        setContentView(R.layout.activity_classtable);
//        tableCanvas = (TableClass) findViewById(R.id.table);
//        tableCanvas.initDate(this);
//        tableCanvas.setOnClickTableListener(onClickTableListener);
//        TabClass tabClass1 = new TabClass(0, 1, 2, "上机课");
//        TabClass tabClass2 = new TabClass(2, 2, 2, "体育课");
//        TabClass tabClass3 = new TabClass(4, 3, 2, "理论课");
//        ArrayList arrayList = new ArrayList();
//        arrayList.add(tabClass1);
//        arrayList.add(tabClass2);
//        arrayList.add(tabClass3);
//        tableCanvas.addClass(arrayList);
//
//    }
//
//    @Override
//    public void initUi() {
//
//    }
//
//    @Override
//    public void loadData() {
//
//    }
//
//    @Override
//    public void onBackPressed() {
//        System.exit(0);
//        android.os.Process.killProcess(android.os.Process.myUid());
//    }
//}
