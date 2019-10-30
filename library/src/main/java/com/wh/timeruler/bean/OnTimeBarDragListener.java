package com.wh.timeruler.bean;

/**
 * 时间轴移动、拖动的回调
 * @author wuhan
 * @date 2018/11/22 19:55
 */
public interface OnTimeBarDragListener {
    /**
     * 当拖动的时候回调
     *
     * @param isLeftDrag
     * @param currentTime
     */
    void onDragTimeBar(boolean isLeftDrag, long currentTime);

    /**
     * 当拖动完成时回调
     *
     * @param currentTime
     */
    void onDragTimeBarFinish(long currentTime);

    /**
     * 拖动到右边范围
     * @param currentTime
     */
    //void onDragTimeBarRightMost(long currentTime);

    /**
     * 拖动到左边范围
     * @param currentTime
     */
    //void onDragTimeBarLeftMost(long currentTime);

    /**
     * 手指抬起
     */
    void onTimeBarActionUp();

    /**
     * 手指按下
     */
    void onTimeBarActionDown();
}
