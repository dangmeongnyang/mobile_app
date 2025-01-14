package com.example.termproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class NotificationSettingsActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("NotificationSettings", Context.MODE_PRIVATE);

        // 뒤로가기 버튼 동작 추가
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 알림 ON/OFF 버튼
        Button btnEnableNotification = findViewById(R.id.btnEnableNotification);
        Button btnDisableNotification = findViewById(R.id.btnDisableNotification);

        btnEnableNotification.setOnClickListener(v -> {
            sharedPreferences.edit().putBoolean("notifications_enabled", true).apply();
            setDailyNotification();
            Toast.makeText(this, "알림이 활성화되었습니다.", Toast.LENGTH_SHORT).show();
        });

        btnDisableNotification.setOnClickListener(v -> {
            sharedPreferences.edit().putBoolean("notifications_enabled", false).apply();
            cancelDailyNotification();
            Toast.makeText(this, "알림이 비활성화되었습니다.", Toast.LENGTH_SHORT).show();
        });

        // 알림 시간 설정 버튼
        TimePicker timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        Button btnSetTime = findViewById(R.id.btnSetTime);
        btnSetTime.setOnClickListener(v -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            sharedPreferences.edit().putInt("notification_hour", hour).putInt("notification_minute", minute).apply();
            setDailyNotification();
            Toast.makeText(this, "알림 시간이 설정되었습니다.", Toast.LENGTH_SHORT).show();
        });
    }

    private void setDailyNotification() {
        int hour = sharedPreferences.getInt("notification_hour", 9);
        int minute = sharedPreferences.getInt("notification_minute", 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1); // 이미 시간이 지난 경우 다음 날로 설정
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
    }


    private void cancelDailyNotification() {
        if (alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }
        Intent intent = new Intent(this, NotificationReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(alarmIntent);
    }

}
