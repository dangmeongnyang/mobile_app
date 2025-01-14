package com.example.termproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHistory;
    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // 뒤로 가기 버튼
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 기록 데이터 로드 및 정렬
        List<String> historyList = loadAndSortRecords();

        // RecyclerView 설정
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new HistoryAdapter(historyList, record -> {
            // 기록 클릭 시 활동 기록 화면으로 이동
            Intent intent = new Intent(HistoryActivity.this, ActivityLogActivity.class);
            intent.putExtra("record_date", record.split(":")[0]); // 날짜만 전달
            startActivity(intent);
        });
        recyclerViewHistory.setAdapter(historyAdapter);
    }

    /**
     * SharedPreferences에서 데이터를 로드하고 날짜순으로 정렬하여 반환
     */
    private List<String> loadAndSortRecords() {
        SharedPreferences sharedPreferences = getSharedPreferences("ActivityLog", Context.MODE_PRIVATE);
        Map<String, ?> allRecords = sharedPreferences.getAll();

        // 날짜별 데이터만 추출 (이미지 URI 제외)
        TreeMap<String, String> sortedRecords = new TreeMap<>(); // TreeMap으로 정렬
        for (Map.Entry<String, ?> entry : allRecords.entrySet()) {
            if (!entry.getKey().endsWith("_photo")) { // "_photo" 키 제외
                sortedRecords.put(entry.getKey(), entry.getValue().toString());
            }
        }

        // TreeMap에서 정렬된 데이터를 리스트로 변환
        List<String> historyList = new ArrayList<>();
        for (Map.Entry<String, String> entry : sortedRecords.entrySet()) {
            historyList.add(entry.getKey() + ": " + entry.getValue());
        }

        return historyList;
    }
}
