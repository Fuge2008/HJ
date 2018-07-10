//package com.haoji.haoji.ui;
//
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.content.pm.ResolveInfo;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.haoji.haoji.R;
//import com.haoji.haoji.service.BadgeIntentService;
//
//import me.leolin.shortcutbadger.ShortcutBadger;
//
//public class BadgeIntentActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_badge_intent);
//
//        final EditText numInput = (EditText) findViewById(R.id.numInput);
//
//        Button button = (Button) findViewById(R.id.btnSetBadge);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int badgeCount = 0;
//                try {
//                    badgeCount = Integer.parseInt(numInput.getText().toString());
//                } catch (NumberFormatException e) {
//                    Toast.makeText(getApplicationContext(), "Error input", Toast.LENGTH_SHORT).show();
//                }
//
//                boolean success = ShortcutBadger.applyCount(BadgeIntentActivity.this, badgeCount);
//
//                Toast.makeText(getApplicationContext(), "Set count=" + badgeCount + ", success=" + success, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        Button launchNotification = (Button) findViewById(R.id.btnSetBadgeByNotification);
//        launchNotification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int badgeCount = 0;
//                try {
//                    badgeCount = Integer.parseInt(numInput.getText().toString());
//                } catch (NumberFormatException e) {
//                    Toast.makeText(getApplicationContext(), "Error input", Toast.LENGTH_SHORT).show();
//                }
//
//                finish();
//                startService(
//                        new Intent(BadgeIntentActivity.this, BadgeIntentService.class).putExtra("badgeCount", badgeCount)
//                );
//            }
//        });
//
//        Button removeBadgeBtn = (Button) findViewById(R.id.btnRemoveBadge);
//        removeBadgeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                boolean success = ShortcutBadger.removeCount(BadgeIntentActivity.this);
//
//                Toast.makeText(getApplicationContext(), "success=" + success, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//        //find the home launcher Package
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
//        String currentHomePackage = resolveInfo.activityInfo.packageName;
//
//        TextView textViewHomePackage = (TextView) findViewById(R.id.textViewHomePackage);
//        textViewHomePackage.setText("launcher:" + currentHomePackage);
//    }
//}
