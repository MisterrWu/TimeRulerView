package com.wh.timeruler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.wh.timeruler.bean.ScaleMode;
import com.wh.timeruler.bean.TimeSlot;
import com.wh.timeruler.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author wuhan
 * @date 2018/11/21 17:58
 */
class RulerAdapter extends RecyclerView.Adapter<RulerAdapter.RulerViewHolder> {
    private static final String TAG = "RulerAdapter";

    private final long HALF_DAY = 12 * 60 * 60;
    private Context context;
    private long startTimeSecond;
    private long endTimeSecond;

    private long timePerSecond;

    private boolean drawDownLine;
    private boolean drawUpLine;
    private boolean drawUpRuler;
    private boolean drawDownRuler;
    private int largeRulerColor;
    private float rulerHeightBig;
    private float rulerHeightSamll;
    private float rulerWidthBig;
    private float rulerWidthSamll;
    private int smallRulerColor;
    private int textColor;
    private float textMarginBottom;
    private float textSize;
    private int upAndDownLineColor;
    private float upAndDownLineWidth;
    private float rulerSpacing;

    RulerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RulerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RulerItemView itemView = new RulerItemView(context);
        return new RulerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RulerViewHolder holder, int position) {
        //int itemWidth = (int) (rulerSpacing + zoom);
        holder.view.setCurTimeIndex(position);
        holder.view.setStartTimeMillisecond(position * timePerSecond + startTimeSecond);
        holder.view.setScaleMode(scaleMode);
        holder.view.setVedioTimeSlot(vedioTimeSlot);
        holder.view.setLayoutParams(new RecyclerView.LayoutParams((int) rulerSpacing, ViewGroup.LayoutParams.MATCH_PARENT));
    }


    public long getStartTime() {
        return startTimeSecond;
    }

    /**
     * 一个item十分钟,
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return (int) ((endTimeSecond - startTimeSecond) / timePerSecond + 1);
    }

    /**
     * 视频时间段集合
     */
    private List<TimeSlot> vedioTimeSlot = new ArrayList<>();

    /**
     * 设置视频时间段
     *
     * @param vedioTimeSlot
     */
    public void setVedioTimeSlot(List<TimeSlot> vedioTimeSlot) {
        if(vedioTimeSlot == null){
            return;
        }
        Log.e(TAG, "setVedioTimeSlot: "+vedioTimeSlot);
        this.vedioTimeSlot.clear();
        this.vedioTimeSlot.addAll(vedioTimeSlot);
        if(!vedioTimeSlot.isEmpty()) {
            TimeSlot firstTimeSlot = vedioTimeSlot.get(0);
            if(firstTimeSlot != null) {
                startTimeSecond = DateUtils.getTimeHour(firstTimeSlot.getStartTimeSecond()) - HALF_DAY;
            }
        }else {
            startTimeSecond = DateUtils.getTimeHour(System.currentTimeMillis() / 1000L) - HALF_DAY;
        }
        endTimeSecond = DateUtils.getTimeHour(System.currentTimeMillis() / 1000L) + HALF_DAY;
        notifyDataSetChanged();
    }

    public long getFirstTimeSlotStartTimeSecond() {
        if(!vedioTimeSlot.isEmpty()) {
            TimeSlot firstTimeSlot = vedioTimeSlot.get(0);
            if (firstTimeSlot != null) {
                return firstTimeSlot.getStartTimeSecond();
            }
        }
        return 0;
    }

    /*public long getLastTimeSlotEndTimeSecond() {
        TimeSlot lastTimeSlot = vedioTimeSlot.get(vedioTimeSlot.size() - 1);
        if(lastTimeSlot != null) {
            return lastTimeSlot.getEndTimeSecond();
        }
        return System.currentTimeMillis()/1000;
    }*/

    class RulerViewHolder extends RecyclerView.ViewHolder {
        private RulerItemView view;

        RulerViewHolder(RulerItemView itemView) {
            super(itemView);
            view = itemView;
            view.setDrawDownLine(drawDownLine);
            view.setDrawUpLine(drawUpLine);
            view.setDrawUpRuler(drawUpRuler);
            view.setDrawDownRuler(drawDownRuler);
            view.setLargeRulerColor(largeRulerColor);
            view.setRulerHeightBig(rulerHeightBig);
            view.setRulerHeightSamll(rulerHeightSamll);
            view.setRulerWidthBig(rulerWidthBig);
            view.setRulerWidthSamll(rulerWidthSamll);
            view.setSmallRulerColor(smallRulerColor);
            view.setTextColor(textColor);
            view.setTextMarginBottom(textMarginBottom);
            view.setTextSize(textSize);
            view.setTimePer(timePerSecond);
            view.setUpAndDownLineColor(upAndDownLineColor);
            view.setUpAndDownLineWidth(upAndDownLineWidth);
        }
    }

    //private float zoom;

    /**
     * 设置缩放值
     *
     * @param zoom
     */
    /*public void setZoom(float zoom) {
        this.zoom = zoom;
        notifyDataSetChanged();
    }*/

    private ScaleMode scaleMode;

    public void setScaleMode(ScaleMode scaleMode) {
        this.scaleMode = scaleMode;
        notifyDataSetChanged();
    }

    public void setDrawDownLine(boolean drawDownLine) {
        this.drawDownLine = drawDownLine;
    }

    public void setDrawUpLine(boolean drawUpLine) {
        this.drawUpLine = drawUpLine;
    }

    public void setDrawUpRuler(boolean drawUpRuler) {
        this.drawUpRuler = drawUpRuler;
    }

    public void setDrawDownRuler(boolean drawDownRuler) {
        this.drawDownRuler = drawDownRuler;
    }

    public void setLargeRulerColor(int largeRulerColor) {
        this.largeRulerColor = largeRulerColor;
    }

    public void setRulerHeightBig(float rulerHeightBig) {
        this.rulerHeightBig = rulerHeightBig;
    }

    public void setRulerHeightSamll(float rulerHeightSamll) {
        this.rulerHeightSamll = rulerHeightSamll;
    }

    public void setRulerWidthBig(float rulerWidthBig) {
        this.rulerWidthBig = rulerWidthBig;
    }

    public void setRulerWidthSamll(float rulerWidthSamll) {
        this.rulerWidthSamll = rulerWidthSamll;
    }

    public void setSmallRulerColor(int smallRulerColor) {
        this.smallRulerColor = smallRulerColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setTextMarginBottom(float textMarginBottom) {
        this.textMarginBottom = textMarginBottom;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setUpAndDownLineColor(int upAndDownLineColor) {
        this.upAndDownLineColor = upAndDownLineColor;
    }

    public void setUpAndDownLineWidth(float upAndDownLineWidth) {
        this.upAndDownLineWidth = upAndDownLineWidth;
    }

    public void setTimePerSecond(long timePerSecond) {
        this.timePerSecond = timePerSecond;
    }

    public void setRulerSpacing(float rulerSpacing) {
        this.rulerSpacing = rulerSpacing;
        notifyDataSetChanged();
    }
}
