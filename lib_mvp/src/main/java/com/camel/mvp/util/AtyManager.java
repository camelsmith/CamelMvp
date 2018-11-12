package com.camel.mvp.util;

import android.app.Activity;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.util.Stack;

/**
 * Created by CamelLuo on 2018/8/3.
 */
public class AtyManager {
    private static final String TAG = AtyManager.class.getSimpleName();
    private static volatile AtyManager mInstance;
    /**
     * 保存所有Activity
     */
    private volatile Stack<Activity> mStack = new Stack<Activity>();

    private AtyManager() {
    }

    /**
     * 创建单例类，提供静态方法调用
     *
     * @return AtyManager
     */
    public static AtyManager getInstance() {
        if (mInstance == null) {
            mInstance = new AtyManager();
        }
        return mInstance;
    }

    /**
     * 将当前Activity推入栈中
     *
     * @param aty Activity
     */
    public void pushAty(Activity aty) {
        Logger.t(TAG).d( "pushAty: " + aty.getLocalClassName());
        mStack.add(aty);
    }

    /**
     * 退出Activity
     *
     * @param aty Activity
     */
    public void popAty(Activity aty) {
        if (aty != null) {
            Logger.t(TAG).d( "popAty: " + aty.getLocalClassName());
            mStack.remove(aty);
        }
    }

    /**
     * 获得当前栈顶的Activity
     *
     * @return Activity Activity
     */
    public Activity getTopAty() {
        Activity aty = null;
        if (!mStack.empty()) {
            aty = mStack.lastElement();
        }
        return aty;
    }

    /**
     * 仅保留栈底
     */
    public void popAllTopAty() {
        int size = mStack.size();
        for (int i = size - 1; i > 0; i--) {
            Activity aty = mStack.get(i);
            if (aty != null) {
                aty.finish();
            }
            mStack.remove(i);
        }
        Logger.t(TAG).d( "popAllTopAty: size = " + mStack.size());
    }

    /**
     * 仅保留栈顶
     */
    public void popAllBottomAty() {
        int size = mStack.size();
        for (int i = size - 2; i >= 0; i--) {
            Activity aty = mStack.get(i);
            if (aty != null) {
                aty.finish();
            }
            mStack.remove(i);
        }
        Logger.t(TAG).d( "popAllTopAty: size = " + mStack.size());
    }

    /**
     * 退出栈中其他所有Activity
     *
     * @param cls Class 类名
     */
    @SuppressWarnings("rawtypes")
    public void popOtherAty(Class cls) {
        if (null == cls) {
            Logger.t(TAG).d( "cls is null");
            return;
        }

        for (Activity activity : mStack) {
            if (null == activity || activity.getClass().equals(cls)) {
                continue;
            }

            activity.finish();
        }
        Logger.t(TAG).d( "activity num is : " + mStack.size());
    }

    /**
     * 退出栈中其他所有Activity
     *
     * @param className String 类名
     */
    @SuppressWarnings("rawtypes")
    public void popOtherAty(String className) {
        if (TextUtils.isEmpty(className)) {
            Logger.t(TAG).d( "className is null");
            return;
        }

        for (Activity activity : mStack) {
            if (activity != null) {
                Logger.t(TAG).d( "popOtherAty: " + activity.getLocalClassName());
            }
            if (null == activity || activity.getLocalClassName().contains(className)) {
                Logger.t(TAG).i( "popOtherAty: save " + activity.getLocalClassName());
                continue;
            }

            activity.finish();
        }
        Logger.t(TAG).d( "activity num is : " + mStack.size());
    }

    /**
     * 退出栈中所有Activity
     */
    public void popAllAty() {
        while (true) {
            Activity activity = getTopAty();
            if (activity == null) {
                break;
            }
            activity.finish();
            popAty(activity);
        }
        Logger.t(TAG).d( "activity num is : " + mStack.size());
    }
}
