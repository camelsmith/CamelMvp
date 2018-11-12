package com.camel.mvp.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by OISNY on 2016/7/9.
 */
public class ToastUtils {
    static Toast toast = null;
    static Handler mainHandler = null;

    public static void showText(Context context, int resId) {
        show(context, null, resId);
    }

    public static void showText(Context context, String msg) {
        show(context, msg, 0);
    }

    private static void show(final Context context, String msg, int resId) {
        if (mainHandler == null) {
            synchronized (ToastUtils.class) {
                if (mainHandler == null) {
                    mainHandler = new Handler(context.getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (msg.obj != null) {
                                showString(context, String.valueOf(msg.obj));
                            } else {
                                showInt(context, msg.arg1);
                            }
                        }
                    };
                }
            }
        }
        Message message = mainHandler.obtainMessage(1, resId, 0, msg);
        mainHandler.sendMessage(message);
    }

    private static void showInt(Context context, int resId) {
        if (toast == null) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        toast.setText(resId);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }

    private static void showString(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
