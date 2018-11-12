package com.camel.mvpdemo;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 * Created by CamelLuo on 2018/10/25.
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initLogger(true);
    }

    /**
     * 定义一个方法用于初始化Logger库
     * @param isDebug 是否需要打印调试信息
     */
    private void initLogger(final boolean isDebug) {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)  //（可选）是否显示线程信息。 默认值为true
                .methodCount(0)         // （可选）要显示的方法行数。 默认2
                .methodOffset(7)        // （可选）隐藏内部方法调用到偏移量。 默认5
                .tag("tc-camelMVP")   //（可选）每个日志的全局标记。 默认PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return isDebug;
            }
        });
    }
}
