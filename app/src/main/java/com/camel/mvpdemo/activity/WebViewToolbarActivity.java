package com.camel.mvpdemo.activity;


import android.os.Bundle;

import com.camel.mvp.activity.BaseWebViewActivity;
import com.camel.mvpdemo.activity.base.AppBaseWebViewActivity;
import com.camel.mvpdemo.presenter.EmptyPresenter;
import com.camel.mvpdemo.viewinterface.EmptyInterface;
import com.orhanobut.logger.Logger;

/**
 * Created by CamelLuo on 2018/10/26.
 */
public class WebViewToolbarActivity extends AppBaseWebViewActivity<EmptyInterface, EmptyPresenter> implements EmptyInterface {
    private static final String TAG = WebViewToolbarActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.t(TAG).e(TAG + "   正在执行 onCreate()");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        Logger.t(TAG).e(TAG + "   正在执行 initView()");
    }

    @Override
    protected boolean isUseCookie() {
        return false;
    }

    @Override
    protected boolean canDebugByChromeDevTools() {
        return false;
    }

    @Override
    protected boolean isUseOfflineWebUrl() {
        return false;
    }

    @Override
    protected void doOnPageFinished() {

    }

    @Override
    protected boolean isNeedToolbar() {
        return true;
    }

    @Override
    protected boolean isNeedStatusBarFontDark() {
        return false;
    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}
