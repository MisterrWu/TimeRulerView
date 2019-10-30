package com.wh.timeruler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;

import com.wh.timeruler.bean.OnTimeBarDragListener;
import com.wh.timeruler.bean.ScaleMode;
import com.wh.timeruler.bean.TimeSlot;
import com.wh.timeruler.utils.DisplayUtils;

import java.util.List;


/**
 * @author wuhan
 * @date 2018/11/21 20:42
 */
public class RulerView extends RecyclerView implements ScaleGestureDetector.OnScaleGestureListener, ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = "RulerView";
    private final int MIN_SPAN = 10;
    /**
     * 单位秒
     */
    private long timePerSecond = 10 * 60;

    private float minSpan = DisplayUtils.dip2px(MIN_SPAN);

    /**
     * 三级
     */
    private float maxSpan = DisplayUtils.dip2px(MIN_SPAN * 2 * 2 * 2);

    /**
     * 默认第二级
     */
    private float rulerSpacing = DisplayUtils.dip2px(MIN_SPAN * 2);
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
    private int centerLineColor;
    private float centerLineWidth;

    private ScaleGestureDetector scaleGestureDetector;

    /**
     * 记录缩放前的时间
     */
    private long lastTimeSecond;

    /**
     * 刻度缩放值
     */
    //private float zoom;

    /**
     * 当前时间的毫秒值
     */
    private long currentTimeSecond;
    /**
     * 滑动结果回调
     */
    private OnTimeBarDragListener onTimeBarDragListener;
    /**
     * 线性布局
     */
    private MyLinearLayoutManager manager;

    /**
     * 中心点距离左边所占用的时长
     */
    private long centerPointDuration;
    /**
     * 中轴线画笔
     */
    private Paint centerLinePaint = new Paint();

    /**
     * 是否自动滚动
     */
    private int mScrollState = RecyclerView.SCROLL_STATE_IDLE;
    /**
     * 是否在缩放
     */
    private boolean isScale = false;

    /**
     * 适配器
     */
    private RulerAdapter adapter;
    /**
     * 缩放模式
     */
    private ScaleMode scaleMode = ScaleMode.KEY_HALF_HOUSE;

    private int currViewWidth;

    /**
     * 每次按下只缩放一次
     */
    private boolean isOnceScale = false;

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RulerView);
            parseTypedArray(ta);
            ta.recycle();
            init(context);
            initAdapter(context);
            getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
    }

    private class MyLinearLayoutManager extends LinearLayoutManager {
        private boolean iscanScrollHorizontally = true;

        public MyLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public boolean canScrollHorizontally() {
            return iscanScrollHorizontally;
        }

        public void setIscanScrollHorizontally(boolean iscanScrollHorizontally) {
            this.iscanScrollHorizontally = iscanScrollHorizontally;
        }
    }

    /**
     * 设置是否可以滑动
     *
     * @param isCanScrollBar
     */
    public void setIsCanScrollBar(boolean isCanScrollBar) {
        if (manager != null) {
            manager.setIscanScrollHorizontally(isCanScrollBar);
        }
    }

    private void init(final Context context) {
        initPaint();
        scaleGestureDetector = new ScaleGestureDetector(context, this);
    }


    private void parseTypedArray(TypedArray ta) {
        centerLineColor = ta.getColor(R.styleable.RulerView_centerLineColor, 0xFFFF4545);
        centerLineWidth = ta.getDimension(R.styleable.RulerView_centerLineWidth, DisplayUtils.dip2px(1));
        drawDownLine = ta.getBoolean(R.styleable.RulerView_drawDownLine, false);
        drawDownRuler = ta.getBoolean(R.styleable.RulerView_drawDownRuler, true);
        drawUpLine = ta.getBoolean(R.styleable.RulerView_drawUpLine, false);
        drawUpRuler = ta.getBoolean(R.styleable.RulerView_drawUpRuler, false);
        largeRulerColor = ta.getColor(R.styleable.RulerView_largeRulerColor, 0xffffffff);
        rulerHeightBig = ta.getDimension(R.styleable.RulerView_rulerHeightBig, DisplayUtils.dip2px(10));
        rulerHeightSamll = ta.getDimension(R.styleable.RulerView_rulerHeightSamll, DisplayUtils.dip2px(6));
        rulerWidthBig = ta.getDimension(R.styleable.RulerView_rulerWidthBig, DisplayUtils.dip2px(1f));
        rulerWidthSamll = ta.getDimension(R.styleable.RulerView_rulerWidthSamll, DisplayUtils.dip2px(1f));
        smallRulerColor = ta.getColor(R.styleable.RulerView_smallRulerColor, 0x4cffffff);
        textColor = ta.getColor(R.styleable.RulerView_textColor, 0xffffffff);
        textMarginBottom = ta.getDimension(R.styleable.RulerView_textMarginBottom, DisplayUtils.dip2px(6));
        textSize = ta.getDimension(R.styleable.RulerView_textSize, DisplayUtils.dip2px(10));
        upAndDownLineColor = ta.getColor(R.styleable.RulerView_upAndDownLineColor, 0x4cffffff);
        upAndDownLineWidth = ta.getDimension(R.styleable.RulerView_upAndDownLineWidth, DisplayUtils.dip2px(1));
    }

    private void initAdapter(Context context) {
        manager = new MyLinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(manager);
        adapter = new RulerAdapter(context);
        adapter.setRulerSpacing(rulerSpacing);
        adapter.setScaleMode(scaleMode);
        adapter.setTimePerSecond(timePerSecond);
        adapter.setDrawDownLine(drawDownLine);
        adapter.setDrawDownRuler(drawDownRuler);
        adapter.setDrawUpLine(drawUpLine);
        adapter.setDrawUpRuler(drawUpRuler);
        adapter.setLargeRulerColor(largeRulerColor);
        adapter.setRulerHeightBig(rulerHeightBig);
        adapter.setRulerHeightSamll(rulerHeightSamll);
        adapter.setRulerWidthBig(rulerWidthBig);
        adapter.setRulerWidthSamll(rulerWidthSamll);
        adapter.setSmallRulerColor(smallRulerColor);
        adapter.setTextColor(textColor);
        adapter.setTextMarginBottom(textMarginBottom);
        adapter.setTextSize(textSize);
        adapter.setUpAndDownLineColor(upAndDownLineColor);
        adapter.setUpAndDownLineWidth(upAndDownLineWidth);
        setAdapter(adapter);
    }

    /**
     * 设置视频时间段
     *
     * @param vedioTimeSlot
     */
    public void setVedioTimeSlot(List<TimeSlot> vedioTimeSlot) {
        adapter.setVedioTimeSlot(vedioTimeSlot);
        if (currentTimeSecond == 0) {
            setCurrentTimeSecond(System.currentTimeMillis() / 1000);
        } else {
            updateCenteLinePostion();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_POINTER_DOWN) {
            isScale = true;
        }
        final boolean streamComplete = action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_CANCEL;
        if (action == MotionEvent.ACTION_DOWN || streamComplete) {
            isScale = false;
        }
        if (action == MotionEvent.ACTION_DOWN) {
            if (onTimeBarDragListener != null) {
                onTimeBarDragListener.onTimeBarActionDown();
            }
        } else if (streamComplete) {
            if (onTimeBarDragListener != null) {
                onTimeBarDragListener.onTimeBarActionUp();
            }
        }
        return isScale || super.onTouchEvent(event);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        if (mScrollState == RecyclerView.SCROLL_STATE_IDLE) {
            return;
        }
        if (isScale) {
            return;
        }
        View firstVisibleItem = manager.findViewByPosition(manager.findFirstVisibleItemPosition());
        if (firstVisibleItem == null) {
            return;
        }
        //第一个可见item的位置
        int firstVisableItemPosition = manager.findFirstVisibleItemPosition();
        //获取左屏幕的偏移量
        long offsetTime = (long) (timePerSecond * (Math.abs(firstVisibleItem.getLeft()) / (float) firstVisibleItem.getWidth()) + firstVisableItemPosition * timePerSecond);
        currentTimeSecond = adapter.getStartTime() + offsetTime + centerPointDuration;
        //实时回调拖动时间
        if (onTimeBarDragListener != null) {
            onTimeBarDragListener.onDragTimeBar(dx > 0, currentTimeSecond);
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        Log.e(TAG, "onScrollStateChanged: "+state+",currentTimeSecond "+currentTimeSecond);
        mScrollState = state;
        if (state == 0) {
            long currentSystemTimeS = System.currentTimeMillis() / 1000;
            if (currentTimeSecond < adapter.getFirstTimeSlotStartTimeSecond()) {
                //超过回看最左时间,回到最左时间
                setCurrentTimeSecond(adapter.getFirstTimeSlotStartTimeSecond());
            } else if (currentTimeSecond > currentSystemTimeS) {
                //超过回看最右时间,回到最右时间
                setCurrentTimeSecond(currentSystemTimeS);
            }
            if (onTimeBarDragListener != null) {
                onTimeBarDragListener.onDragTimeBarFinish(currentTimeSecond);
            }
        }
    }

    /**
     * @param detector
     * @return
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if(isOnceScale){
            return onceScale(detector);
        }else {
            return normalScale(detector);
        }
    }

    private boolean onceScale(ScaleGestureDetector detector){
        float currDirection = detector.getCurrentSpan() - detector.getPreviousSpan();
        //方向变化了
        if(currDirection < 0 && direction > 0){
            beginSpan = detector.getCurrentSpan();
        }else if(currDirection > 0 && direction < 0){
            beginSpan = detector.getCurrentSpan();
        }
        direction = currDirection;
        // 变化的长度
        float distanceSpan = detector.getCurrentSpan() - beginSpan;
        //Log.e(TAG, "onScale: distanceSpan "+distanceSpan+",beginSpan "+beginSpan);
        if (Math.abs(distanceSpan) > DisplayUtils.dip2px(5)) {
            //双指缩放了
            if (distanceSpan > 0) {
                if(alreadyEnlarge){
                    return true;
                }
                rulerSpacing = rulerSpacing * 2f;
                alreadyEnlarge = true;
                alreadyNarrow = false;
            } else {
                if(alreadyNarrow){
                    return true;
                }
                rulerSpacing = rulerSpacing / 2f;
                alreadyNarrow = true;
                alreadyEnlarge = false;
            }
            if(rulerSpacing < minSpan){
                rulerSpacing = minSpan;
            }else if (rulerSpacing > maxSpan){
                rulerSpacing = maxSpan;
            }
            if(rulerSpacing == maxSpan){
                scaleMode = ScaleMode.KEY_MINUTE;
            }else {
                scaleMode = ScaleMode.KEY_HALF_HOUSE;
            }
            Log.e(TAG, "onScale: rulerSpacing "+rulerSpacing);
            adapter.setScaleMode(scaleMode);
            adapter.setRulerSpacing(rulerSpacing);
            centerPointDuration = (long) (((getWidth() / 2f) / (rulerSpacing)) * timePerSecond);
            setCurrentTimeSecond(lastTimeSecond);
        }
        return true;
    }

    private boolean normalScale(ScaleGestureDetector detector){
        float currDirection = detector.getCurrentSpan() - detector.getPreviousSpan();
        if(Math.abs(currDirection) > 2){
            if(currDirection > 0){
                rulerSpacing += 5;
            }else {
                rulerSpacing -= 5;
            }
        }else{
            return true;
        }
        if(rulerSpacing < minSpan){
            rulerSpacing = minSpan;
        }else if (rulerSpacing > maxSpan){
            rulerSpacing = maxSpan;
        }
        if(rulerSpacing == maxSpan){
            scaleMode = ScaleMode.KEY_MINUTE;
        }else {
            scaleMode = ScaleMode.KEY_HALF_HOUSE;
        }
        Log.e(TAG, "onScale: rulerSpacing "+rulerSpacing);
        adapter.setScaleMode(scaleMode);
        adapter.setRulerSpacing(rulerSpacing);
        centerPointDuration = (long) (((getWidth() / 2f) / (rulerSpacing)) * timePerSecond);
        setCurrentTimeSecond(lastTimeSecond);
        return true;
    }

    boolean alreadyEnlarge = false;
    boolean alreadyNarrow = false;
    float beginSpan; //开始距离
    float direction; //方向

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        beginSpan = detector.getCurrentSpan();
        direction = 0;
        alreadyEnlarge = false;
        alreadyNarrow = false;
        Log.e(TAG, "onScaleBegin: ");
        //双指按下的时候，需要静止滑动
        setIsCanScrollBar(false);
        lastTimeSecond = currentTimeSecond;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        Log.e(TAG, "onScaleEnd: ");
        //双指抬起
        setIsCanScrollBar(true);
    }

    /**
     * 设置当前时间
     *
     * @param currentTimeSecond
     */
    public synchronized void setCurrentTimeSecond(long currentTimeSecond) {
        this.currentTimeSecond = currentTimeSecond;
        lastTimeSecond = currentTimeSecond;
        updateCenteLinePostion();
    }


    /**
     * 更新中心点的位置
     */
    public void updateCenteLinePostion() {
        long leftTime = this.currentTimeSecond - centerPointDuration;
        //根据左边时间计算第一个可以显示的下标
        int leftTimeIndex = (int) ((leftTime - adapter.getStartTime()) / (timePerSecond));
        //计算偏移量
        long offset = (long) (((rulerSpacing) / (timePerSecond)) * (leftTime - adapter.getStartTime()));
        //滑动到指定的item并设置偏移量(offset不能超过rulerSpacing)
        manager.scrollToPositionWithOffset(leftTimeIndex, (int) (-offset % (rulerSpacing)));
    }


    /**
     * 初始化画笔
     */
    private void initPaint() {
        centerLinePaint.setAntiAlias(true);
        centerLinePaint.setStrokeWidth(centerLineWidth);
        centerLinePaint.setColor(centerLineColor);
    }

    /**
     * 画中心线
     *
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawCenterLine(canvas);
    }

    /**
     * 画中间线
     *
     * @param canvas
     */
    private void drawCenterLine(Canvas canvas) {
        canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), centerLinePaint);
    }

    /**
     * 设置移动监听
     *
     * @param onTimeBarDragListener
     */
    public void setOnTimeBarDragListener(OnTimeBarDragListener onTimeBarDragListener) {
        this.onTimeBarDragListener = onTimeBarDragListener;
    }

    @Override
    public void onGlobalLayout() {
        //Log.e(TAG, "onGlobalLayout: currViewWidth "+currViewWidth + ",ViewWidth "+getWidth());
        if(currViewWidth == 0 && getWidth() > 0){
            currViewWidth = getWidth();
            centerPointDuration = (long) (((currViewWidth / 2f) / (rulerSpacing)) * timePerSecond);
            updateCenteLinePostion();
        }else if(currViewWidth > 0 && getWidth() > 0 && currViewWidth != getWidth()){
            currViewWidth = getWidth();
            centerPointDuration = (long) (((currViewWidth / 2f) / (rulerSpacing)) * timePerSecond);
            updateCenteLinePostion();
        }
    }

}
