package com.wh.timeruler.utils;

import android.content.res.Resources;

/**
 * 
 * @author wuhan
 * @date 2018/11/23 10:23
 */
public class DisplayUtils {
    /**
     * dp转px
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    public static int px2dip(int px) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
