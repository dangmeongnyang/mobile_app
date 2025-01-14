package com.example.termproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 버튼 연결
        Button btnActivityLog = findViewById(R.id.btnActivityLog);
        Button btnTakePhoto = findViewById(R.id.btnTakePhoto);
        Button btnHistory = findViewById(R.id.btnHistory);
        Button btnNotificationSettings = findViewById(R.id.btnNotificationSettings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 버튼 클릭 이벤트
        btnActivityLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 활동 기록 화면으로 이동
                Intent intent = new Intent(MainActivity.this, ActivityLogActivity.class);
                startActivity(intent);
            }
        });

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사진 촬영 화면으로 이동
                Intent intent = new Intent(MainActivity.this, TakePhotoActivity.class);
                startActivity(intent);
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 히스토리 화면으로 이동
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        btnNotificationSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 알림 설정 화면으로 이동
                Intent intent = new Intent(MainActivity.this, NotificationSettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}
