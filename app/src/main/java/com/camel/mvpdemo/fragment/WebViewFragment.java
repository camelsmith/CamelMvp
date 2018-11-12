package com.camel.mvpdemo.fragment;

import android.os.Bundle;
import android.view.View;

import com.camel.mvp.base.MvpBasePresenter;
import com.camel.mvpdemo.fragment.base.AppBaseWebViewFragment;
import com.camel.mvpdemo.presenter.EmptyPresenter;
import com.camel.mvpdemo.viewinterface.EmptyInterface;

/**
 * Created by CamelLuo on 2018/11/2.
 */
public class WebViewFragment extends AppBaseWebViewFragment<EmptyInterface, EmptyPresenter> implements EmptyInterface {

    @Override
    protected void doOnToolbarBackClick(View v) {
        super.doOnToolbarBackClick(v);
        mActivity.finish();
    }

    /**
     * 实例化Fragment的入口，可以携带参数传入
     * @return
     */
    public static WebViewFragment newInstance(Bundle bundle) {
        WebViewFragment fragment = new WebViewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 定义一个方法用于设置Bundle内容
     * @param webUrl
     * @return
     */
    public static Bundle setBundleValue(String webUrl) {
        Bundle bundle = new Bundle();
        bundle.putString("webUrl", webUrl);
        return bundle;
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
