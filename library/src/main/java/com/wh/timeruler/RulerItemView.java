package com.wh.timeruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.wh.timeruler.bean.ScaleMode;
import com.wh.timeruler.bean.TimeSlot;
import com.wh.timeruler.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuhan
 * @date 2018/11/21 16:07
 * @hide
 */
class RulerItemView extends View {

    /*********** 时间范围 **************/
    private long timePerSecond;
    private long startTimeSecond;
    private long endTimeSecond;

    /*********** 小刻度刻度配置 **************/
    private Paint smallRulerPaint = new Paint();
    private float rulerHeightSamll;

    /*********** 大刻度刻度配置 **************/
    private Paint largeRulerPaint = new Paint();
    private float rulerHeightBig;
    private boolean isDrawUpRuler;
    private boolean isDrawDownRuler;

    /*********** 上下两条线 **************/
    private Paint upAndDownLinePaint = new Paint();
    private boolean isDrawUpLine;
    private boolean isDrawDownLine;

    /*********** 文本画笔 **************/
    private TextPaint keyTickTextPaint = new TextPaint();
    private float textMarginBottom;

    /*********** 视频区域画笔 **************/
    private Paint vedioAreaPaint = new Paint();
    private RectF vedioAreaRect = new RectF();

    public RulerItemView(Context context) {
        this(context, null);
    }

    public RulerItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPaint(context);
    }

    private void initPaint(Context context) {
        smallRulerPaint.setAntiAlias(true);

        largeRulerPaint.setAntiAlias(true);

        keyTickTextPaint.setAntiAlias(true);

        vedioAreaPaint.setAntiAlias(true);

        upAndDownLinePaint.setAntiAlias(true);
    }

    private ScaleMode scaleMode;

    public ScaleMode getScaleMode() {
        return scaleMode;
    }

    public void setScaleMode(ScaleMode scaleMode) {
        this.scaleMode = scaleMode;
    }

    public void setStartTimeMillisecond(long startTimeSecond) {
        this.startTimeSecond = startTimeSecond;
        endTimeSecond = startTimeSecond + timePerSecond;
    }

    public void setTimePer(long timePerSecond) {
        this.timePerSecond = timePerSecond;
    }

    public void setSmallRulerColor(int smallRulerColor) {
        smallRulerPaint.setColor(smallRulerColor);
    }

    public void setRulerWidthSamll(float rulerWidthSamll) {
        smallRulerPaint.setStrokeWidth(rulerWidthSamll);
    }

    public void setRulerHeightSamll(float rulerHeightSamll) {
        this.rulerHeightSamll = rulerHeightSamll;
    }

    public void setLargeRulerColor(int largeRulerColor) {
        largeRulerPaint.setColor(largeRulerColor);
    }

    public void setRulerWidthBig(float rulerWidthBig) {
        largeRulerPaint.setStrokeWidth(rulerWidthBig);
    }

    public void setRulerHeightBig(float rulerHeightBig) {
        this.rulerHeightBig = rulerHeightBig;
    }

    public void setDrawUpRuler(boolean drawUpRuler) {
        isDrawUpRuler = drawUpRuler;
    }

    public void setDrawDownRuler(boolean drawDownRuler) {
        isDrawDownRuler = drawDownRuler;
    }

    public void setUpAndDownLineWidth(float upAndDownLineWidth) {
        upAndDownLinePaint.setStrokeWidth(upAndDownLineWidth);
    }

    public void setUpAndDownLineColor(int upAndDownLineColor) {
        upAndDownLinePaint.setColor(upAndDownLineColor);
    }

    public void setDrawUpLine(boolean drawUpLine) {
        isDrawUpLine = drawUpLine;
    }

    public void setDrawDownLine(boolean drawDownLine) {
        isDrawDownLine = drawDownLine;
    }

    public void setTextColor(int textColor) {
        keyTickTextPaint.setColor(textColor);
    }

    public void setTextSize(float textSize) {
        keyTickTextPaint.setTextSize(textSize);
    }

    public void setTextMarginBottom(float textMarginBottom) {
        this.textMarginBottom = textMarginBottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDownLine(canvas);
        drawUpLine(canvas);
        drawRuler(canvas);
        drawVedioArea(canvas);
    }

    /**
     * 画视频区域
     *
     * @param canvas
     */
    private void drawVedioArea(Canvas canvas) {
        for (TimeSlot timeSlot : vedioTimeSlot) {
            //1、首先判断是否全部包含了本时间段
            boolean isContainTime = isContainTime(timeSlot);
            boolean isLeftTime = isCurrentTimeArea(timeSlot.getStartTimeSecond());
            boolean isRightTime = isCurrentTimeArea(timeSlot.getEndTimeSecond());
            vedioAreaPaint.setColor(timeSlot.getColor());
            if (isContainTime) {
                //包含所有（画整个item）
                vedioAreaRect.set(0, 0, getWidth(), getHeight());
                canvas.drawRect(vedioAreaRect, vedioAreaPaint);
            } else if (isLeftTime && isRightTime) {
                //两端都在（画左边时刻到右边时刻）
                float distanceX1 = (timeSlot.getStartTimeSecond() - startTimeSecond) * (getWidth() / ((float) timePerSecond));
                float distanceX2 = (timeSlot.getEndTimeSecond() - startTimeSecond) * (getWidth() / ((float) timePerSecond));
                vedioAreaRect.set(distanceX1, 0, distanceX2, getHeight());
                canvas.drawRect(vedioAreaRect, vedioAreaPaint);
            } else if (isLeftTime) {
                //只有左边在（左边时刻开始到item结束都画）
                float distanceX = (timeSlot.getStartTimeSecond() - startTimeSecond) * (getWidth() / ((float) timePerSecond));
                vedioAreaRect.set(distanceX, 0, getWidth(), getHeight());
                canvas.drawRect(vedioAreaRect, vedioAreaPaint);
            } else if (isRightTime) {
                //只有右边在（画从头开始到右边时刻）
                float distanceX = (timeSlot.getEndTimeSecond() - startTimeSecond) * (getWidth() / ((float) timePerSecond));
                vedioAreaRect.set(0, 0, distanceX, getHeight());
                canvas.drawRect(vedioAreaRect, vedioAreaPaint);
            }
        }
    }

    private boolean isContainTime(TimeSlot timeSlot) {
        return startTimeSecond >= timeSlot.getStartTimeSecond() && endTimeSecond <= timeSlot.getEndTimeSecond();
    }

    private boolean isCurrentTimeArea(long nowTime) {
        return nowTime >= startTimeSecond && nowTime <= endTimeSecond;
    }

    /**
     * 画刻度尺
     *
     * @param canvas
     */
    private void drawRuler(Canvas canvas) {
        float viewWidth = getWidth();
        float rightX = 0;
        if (scaleMode == ScaleMode.KEY_HALF_HOUSE) {
            //半小时级别的画法
            float itemWidth = viewWidth / 2;
            for (int i = 0; i < 2; i++) {
                if (i == 0 && (timeIndex) % 3 == 0) {
                    //画上面的大刻度
                    if (isDrawUpRuler) {
                        canvas.drawLine(rightX, 0, rightX, rulerHeightBig, largeRulerPaint);
                    }
                    //画下面的大刻度
                    if (isDrawDownRuler) {
                        canvas.drawLine(rightX, getHeight(), rightX, getHeight() - rulerHeightBig, largeRulerPaint);
                    }
                    rightX += itemWidth;
                    String strTime = DateUtils.formatHourMinute(startTimeSecond * 1000);
                    float timeStrWidth = keyTickTextPaint.measureText(strTime);
                    canvas.drawText(strTime, -timeStrWidth / 2, getHeight() - upAndDownLinePaint.getStrokeWidth() - rulerHeightBig - textMarginBottom, keyTickTextPaint);
                } else {
                    //画上面的小刻度
                    if (isDrawUpRuler) {
                        canvas.drawLine(rightX, 0, rightX, rulerHeightSamll, smallRulerPaint);
                    }
                    //画下面的小刻度
                    if (isDrawDownRuler) {
                        canvas.drawLine(rightX, getHeight(), rightX, getHeight() - rulerHeightSamll, smallRulerPaint);
                    }
                    rightX += itemWidth;
                }
            }
        } else if (scaleMode == ScaleMode.KEY_HOUSE) {
            //小时级别的画法
            //大刻度
            if ((timeIndex) % 6 == 0) {
                //画上面的大刻度
                if (isDrawUpRuler) {
                    canvas.drawLine(0, 0, 0, rulerHeightBig, largeRulerPaint);
                }
                //画下面的大刻度
                if (isDrawDownRuler) {
                    canvas.drawLine(0, getHeight(), 0, getHeight() - rulerHeightBig, largeRulerPaint);
                }
                String strTime = DateUtils.formatHourMinute(startTimeSecond * 1000);
                float timeStrWidth = keyTickTextPaint.measureText(strTime);
                canvas.drawText(strTime, -timeStrWidth / 2, getHeight() - upAndDownLinePaint.getStrokeWidth() - rulerHeightBig - textMarginBottom, keyTickTextPaint);
            } else {//小刻度
                //画上面的小刻度
                if (isDrawUpRuler) {
                    canvas.drawLine(0, 0, 0, rulerHeightSamll, smallRulerPaint);
                }
                //画下面的小刻度
                if (isDrawDownRuler) {
                    canvas.drawLine(0, getHeight(), 0, getHeight() - rulerHeightSamll, smallRulerPaint);
                }
            }
        } else {
            //分钟级别的画法
            float itemWidth = viewWidth / 2;
            for (int i = 0; i < 2; i++) {
                if ((i == 0 && (timeIndex) % 3 == 0)) {
                    //画上面的大刻度
                    if (isDrawUpRuler) {
                        canvas.drawLine(rightX, 0, rightX, rulerHeightBig, largeRulerPaint);
                    }
                    //画下面的大刻度
                    if (isDrawDownRuler) {
                        canvas.drawLine(rightX, getHeight(), rightX, getHeight() - rulerHeightBig, largeRulerPaint);
                    }
                    rightX += itemWidth;
                    String strTime = DateUtils.formatHourMinute(startTimeSecond * 1000);
                    float timeStrWidth = keyTickTextPaint.measureText(strTime);
                    canvas.drawText(strTime, -timeStrWidth / 2, getHeight() - upAndDownLinePaint.getStrokeWidth() - rulerHeightBig - textMarginBottom, keyTickTextPaint);
                } else if(i == 1 && (timeIndex - 1) % 3 == 0){
                    //十五分钟
                    //画上面的大刻度
                    if (isDrawUpRuler) {
                        canvas.drawLine(rightX, 0, rightX, rulerHeightBig, largeRulerPaint);
                    }
                    //画下面的大刻度
                    if (isDrawDownRuler) {
                        canvas.drawLine(rightX, getHeight(), rightX, getHeight() - rulerHeightBig, largeRulerPaint);
                    }
                    rightX += itemWidth;
                    String strTime = DateUtils.formatHourMinute(startTimeSecond * 1000+ timePerSecond / 2*1000);
                    float timeStrWidth = keyTickTextPaint.measureText(strTime);
                    canvas.drawText(strTime, -timeStrWidth / 2 + itemWidth, getHeight() - upAndDownLinePaint.getStrokeWidth() - rulerHeightBig - textMarginBottom, keyTickTextPaint);
                }else {
                    //画上面的小刻度
                    if (isDrawUpRuler) {
                        canvas.drawLine(rightX, 0, rightX, rulerHeightSamll, smallRulerPaint);
                    }
                    //画下面的小刻度
                    if (isDrawDownRuler) {
                        canvas.drawLine(rightX, getHeight(), rightX, getHeight() - rulerHeightSamll, smallRulerPaint);
                    }
                    rightX += itemWidth;
                }
            }
        }
    }

    /**
     * 画顶部条线
     *
     * @param canvas
     */
    private void drawUpLine(Canvas canvas) {
        if (!isDrawUpLine) {
            return;
        }
        int viewWidth = getWidth();
        canvas.drawLine(0, upAndDownLinePaint.getStrokeWidth() / 2, viewWidth, upAndDownLinePaint.getStrokeWidth() / 2, upAndDownLinePaint);
    }

    /**
     * 画底部线条
     *
     * @param canvas
     */
    private void drawDownLine(Canvas canvas) {
        if (!isDrawDownLine) {
            return;
        }
        int viewWidth = getWidth();
        canvas.drawLine(0, getHeight(), viewWidth, getHeight() - upAndDownLinePaint.getStrokeWidth() / 2, upAndDownLinePaint);
    }

    /**
     * 设置当前时间
     *
     * @param index
     */
    public void setCurTimeIndex(int index) {
        timeIndex = index;
    }

    private int timeIndex;

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
        this.vedioTimeSlot.clear();
        for (TimeSlot timeSlot : vedioTimeSlot) {
            boolean isContainTime = isContainTime(timeSlot);
            boolean isLeftTime = isCurrentTimeArea(timeSlot.getStartTimeSecond());
            boolean isRightTime = isCurrentTimeArea(timeSlot.getEndTimeSecond());
            if (isContainTime) {
                this.vedioTimeSlot.add(timeSlot);
            } else if (isLeftTime && isRightTime) {
                this.vedioTimeSlot.add(timeSlot);
            } else if (isLeftTime) {
                this.vedioTimeSlot.add(timeSlot);
            } else if (isRightTime) {
                this.vedioTimeSlot.add(timeSlot);
            }
        }
    }
}
