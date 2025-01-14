package com.example.termproject;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {

    private final HashSet<CalendarDay> dates;
    private final ColorDrawable highlightDrawable;

    public EventDecorator(HashSet<CalendarDay> dates) {
        this.dates = dates;
        this.highlightDrawable = new ColorDrawable(Color.RED); // 빨간색 배경
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day); // 날짜가 기록된 날짜 목록에 포함되어 있는지 확인
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(highlightDrawable); // 날짜의 배경을 빨간색으로 설정
    }
}
