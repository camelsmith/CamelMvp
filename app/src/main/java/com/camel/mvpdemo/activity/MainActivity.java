package com.camel.mvpdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.camel.mvpdemo.R;
import com.camel.mvpdemo.activity.base.AppBaseActivity;
import com.camel.mvpdemo.activity.base.AppBaseWebViewActivity;
import com.camel.mvpdemo.presenter.EmptyPresenter;
import com.camel.mvpdemo.viewinterface.EmptyInterface;
import com.orhanobut.logger.Logger;

public class MainActivity extends AppBaseActivity<EmptyInterface, EmptyPresenter> implements EmptyInterface {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initToolBar(false,"MvpDemo");
        Log.e(TAG, "initView: " );
        Logger.t(TAG).e("初始化成功！");
    }

    @Override
    public void getIntentExtras(Intent intent) {

    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected int setContentViewId() {
        return R.layout.activity_main;
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
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    public void onXmlClick(View view) {
        switch (view.getId()){
            case R.id.activity_main_start_fragment_activity_button:{
                startActivity(FragmentActivity.class);
                break;
            }

            case R.id.activity_main_start_toolbar_fragment_activity_button:{
                startActivity(ToolbarFragmentActivity.class);
                break;
            }

            case R.id.activity_main_start_webView_activity_button:{
                startActivity(WebViewToolbarActivity.class,WebViewToolbarActivity.setBundleValue("www.qq.com"));
                break;
            }

            case R.id.activity_main_start_fragment_webView_activity_button:{
                startActivity(WebViewFragmentActivity.class,WebViewFragmentActivity.setBundleValue("www.qq.com"));
                break;
            }
        }
    }
}
