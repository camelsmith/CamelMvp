package com.camel.mvp.util;

import android.content.Context;

/**
 * Created by CamelLuo on 2018/2/6.
 */

public class ScreenUtil {

    /**
     * 定义一个方法用于获得设备的状态栏的高度
     *
     * @param context 上下文对象
     * @return 高度，单位为px
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

}
