package com.wh.timeruler.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.wh.timeruler.RulerView;
import com.wh.timeruler.bean.OnTimeBarDragListener;
import com.wh.timeruler.bean.TimeSlot;
import com.wh.timeruler.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnTimeBarDragListener {

    private static final String TAG = "RulerView";
    private static final long HALF_DAY = 12 * 60 * 60; // 单位秒
    private static final String IN_FORMAT = "yyyy.MM.dd";
    private static final String CURR_FORMAT = "HH:mm:ss";

    private TextView tvCurrTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvCurrTime = findViewById(R.id.tv_curr_time);
        RulerView mRulerView = findViewById(R.id.ruler_view);
        mRulerView.setOnTimeBarDragListener(this);

        List<TimeSlot> slots = new ArrayList<>();

        TimeSlot slot1 = new TimeSlot();
        slot1.setStartTimeSecond(System.currentTimeMillis() / 1000 - HALF_DAY * 2);
        slot1.setLen(HALF_DAY);
        String showDate = DateUtils.format(IN_FORMAT, slot1.getStartTimeSecond() * 1000L);
        slot1.setShowTime(showDate);

        TimeSlot slot2 = new TimeSlot();
        slot2.setStartTimeSecond(System.currentTimeMillis() / 1000 + HALF_DAY * 2);
        slot2.setLen(HALF_DAY);
        showDate = DateUtils.format(IN_FORMAT, slot2.getStartTimeSecond() * 1000L);
        slot1.setShowTime(showDate);

        slots.add(slot1);
        slots.add(slot2);
        mRulerView.setVedioTimeSlot(slots);
        long currTime = System.currentTimeMillis() / 1000;
        mRulerView.setCurrentTimeSecond(currTime);
        tvCurrTime.setText(timeToString("Init: %d : %s", currTime));
    }

    @Override
    public void onDragTimeBar(boolean isLeftDrag, long currentTime) {
        Log.e(TAG, "onDragTimeBar: " + currentTime);
        tvCurrTime.setText(timeToString("Drag: %d : %s", currentTime));
    }

    @Override
    public void onDragTimeBarFinish(long currentTime) {
        Log.e(TAG, "onDragTimeBarFinish: " + currentTime);
        tvCurrTime.setText(timeToString("Finish: %d : %s", currentTime));
    }

    @Override
    public void onTimeBarActionUp() {
        Log.e(TAG, "onTimeBarActionUp... ");
    }

    @Override
    public void onTimeBarActionDown() {
        Log.e(TAG, "onTimeBarActionDown... ");
    }

    private String timeToString(String format, long currTimeS){
        String forMatTime = DateUtils.format(CURR_FORMAT, currTimeS * 1000);
        return String.format(Locale.getDefault(), format, currTimeS,forMatTime);
    }
}
