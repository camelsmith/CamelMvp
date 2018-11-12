package com.camel.mvp.fragment;

/**
 * Created by Oisny on 2016/6/29.
 */
public interface FragmentSupport {


    BaseFragmentManager getBaseFragmentManager();

    /**
     * 定义一个方法用于在Fragment执行 onResume() 方法时通知注册组件
     */
    void onFragmentResumed();

    /**
     * 定义一个方法用于在Fragment执行 onPause() 方法时通知注册组件
     */
    void onFragmentPaused();

    /**
     * 定义一个方法用于在Fragment执行 onStop() 方法时通知注册组件
     */
    void onFragmentStop();
}
