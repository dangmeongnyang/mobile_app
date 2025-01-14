package com.example.termproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActivityLogActivity extends AppCompatActivity {

    private MaterialCalendarView materialCalendarView;
    private TextView tvDatePrompt, tvRecordDate, tvRecordText;
    private ImageView imgRecordPhoto;
    private Button btnEditRecord, btnUploadPhoto, btnCancel;
    private ImageButton btnBack;
    private LinearLayout dimBackground;

    private String selectedDate = "";
    private HashSet<CalendarDay> recordedDates = new HashSet<>();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("ActivityLog", Context.MODE_PRIVATE);

        // UI 초기화
        materialCalendarView = findViewById(R.id.materialCalendarView);
        tvDatePrompt = findViewById(R.id.tvDatePrompt);
        tvRecordDate = findViewById(R.id.tvRecordDate);
        tvRecordText = findViewById(R.id.tvRecordText);
        imgRecordPhoto = findViewById(R.id.imgRecordPhoto);
        btnEditRecord = findViewById(R.id.btnEditRecord);
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);
        dimBackground = findViewById(R.id.dimBackground);

        // 기록된 날짜 불러오기
        loadRecordedDates();
        updateCalendarView();

        // 뒤로가기 버튼
        btnBack.setOnClickListener(v -> finish());

        // 전달된 날짜가 있으면 선택
        Intent intent = getIntent();
        String selectedHistoryDate = intent.getStringExtra("record_date");
        if (selectedHistoryDate != null) {
            selectDateOnCalendar(selectedHistoryDate);
        }

        // 날짜 선택 이벤트
        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
            selectedDate = date.getYear() + "-" + (date.getMonth() + 1) + "-" + date.getDay();
            String formattedDate = date.getYear() + "년 " + (date.getMonth() + 1) + "월 " + date.getDay() + "일";

            tvRecordDate.setText(formattedDate);

            // 텍스트 기록 불러오기
            String record = sharedPreferences.getString(selectedDate, "내용 없음");
            tvRecordText.setText(record);

            // 이미지 URI 불러오기
            String imageUriString = sharedPreferences.getString(selectedDate + "_photo", null);
            if (imageUriString != null) {
                Uri imageUri = Uri.parse(imageUriString);
                imgRecordPhoto.setImageURI(imageUri);
            } else {
                imgRecordPhoto.setImageURI(null);
            }

            // 회색 배경 및 버튼 표시
            tvDatePrompt.setVisibility(View.GONE);
            dimBackground.setVisibility(View.VISIBLE);
            btnEditRecord.setVisibility(View.VISIBLE);
            btnUploadPhoto.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
        });

        btnEditRecord.setOnClickListener(v -> {
            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "날짜를 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("기록 작성");

            final EditText input = new EditText(this);
            input.setHint("내용을 입력하세요");
            builder.setView(input);

            // 기존 기록 로드
            String existingRecord = sharedPreferences.getString(selectedDate, "");
            input.setText(existingRecord);

            builder.setPositiveButton("저장", (dialog, which) -> {
                String record = input.getText().toString();
                if (!record.isEmpty()) {
                    // 기록 저장
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(selectedDate, record);
                    editor.apply();

                    // 날짜를 기록된 날짜 목록에 추가
                    recordedDates.add(CalendarDay.from(
                            Integer.parseInt(selectedDate.split("-")[0]),
                            Integer.parseInt(selectedDate.split("-")[1]) - 1,
                            Integer.parseInt(selectedDate.split("-")[2])
                    ));
                    saveRecordedDates();
                    updateCalendarView();
                    Toast.makeText(this, "기록이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                resetScreen();
            });

            builder.setNegativeButton("취소", (dialog, which) -> resetScreen());
            builder.show();
        });

        btnUploadPhoto.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, 100);
        });

        btnCancel.setOnClickListener(v -> resetScreen());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(selectedDate + "_photo", selectedImage.toString());
                editor.apply();

                imgRecordPhoto.setImageURI(selectedImage);
                Toast.makeText(this, "사진이 업로드되었습니다.", Toast.LENGTH_SHORT).show();
            }

            // 업로드 완료 후 배경 효과 제거
            resetScreen();
        }
    }

    private void updateCalendarView() {
        materialCalendarView.removeDecorators(); // 기존 데코레이터 제거
        materialCalendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return recordedDates.contains(day);
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.RED));
            }
        });
    }

    private void resetScreen() {
        dimBackground.setVisibility(View.GONE);
        btnEditRecord.setVisibility(View.GONE);
        btnUploadPhoto.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        tvDatePrompt.setVisibility(View.VISIBLE);
    }

    private void selectDateOnCalendar(String date) {
        String[] parts = date.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]) - 1; // CalendarDay의 month는 0-based
        int day = Integer.parseInt(parts[2]);
        CalendarDay selectedDay = CalendarDay.from(year, month, day);

        // 캘린더에서 날짜 선택
        materialCalendarView.setSelectedDate(selectedDay);
        materialCalendarView.setCurrentDate(selectedDay);
    }

    private void saveRecordedDates() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        HashSet<String> datesToSave = new HashSet<>();
        for (CalendarDay day : recordedDates) {
            String dateKey = day.getYear() + "-" + (day.getMonth() + 1) + "-" + day.getDay();
            datesToSave.add(dateKey);
        }
        editor.putStringSet("recordedDates", datesToSave);
        editor.apply();
    }

    private void loadRecordedDates() {
        recordedDates.clear();
        Set<String> savedDates = sharedPreferences.getStringSet("recordedDates", new HashSet<>());
        for (String date : savedDates) {
            String[] parts = date.split("-");
            if (parts.length == 3) {
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]) - 1;
                int day = Integer.parseInt(parts[2]);
                recordedDates.add(CalendarDay.from(year, month, day));
            }
        }
    }
}
