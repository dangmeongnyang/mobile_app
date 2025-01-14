package com.example.termproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<String> historyList;
    private final OnHistoryClickListener clickListener;

    public interface OnHistoryClickListener {
        void onHistoryClick(String record);
    }

    public HistoryAdapter(List<String> historyList, OnHistoryClickListener clickListener) {
        this.historyList = historyList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        // 항목에 번호 추가
        String record = (position + 1) + ". " + historyList.get(position);
        holder.textView.setText(record);

        // 클릭 이벤트 설정
        holder.textView.setOnClickListener(v -> clickListener.onHistoryClick(historyList.get(position)));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
