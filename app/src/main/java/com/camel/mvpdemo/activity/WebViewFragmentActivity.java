package com.camel.mvpdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import com.camel.mvpdemo.R;
import com.camel.mvpdemo.activity.base.AppBaseActivity;
import com.camel.mvpdemo.fragment.WebViewFragment;
import com.camel.mvpdemo.presenter.EmptyPresenter;
import com.camel.mvpdemo.viewinterface.EmptyInterface;

/**
 * Created by CamelLuo on 2018/10/25.
 */
public class WebViewFragmentActivity extends AppBaseActivity<EmptyInterface, EmptyPresenter> implements EmptyInterface {

    private String webUrl;
    private WebViewFragment webViewFragment;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_fragment_webview;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //初始化Fragment管理器
        initBaseFragmentManager(getSupportFragmentManager(),R.id.activity_fragment_webview_content_layout,null);
        //加载Fragment
        if (getBaseFragmentManager() != null){
            webViewFragment = WebViewFragment.newInstance(WebViewFragment.setBundleValue(webUrl));
            getBaseFragmentManager().add(webViewFragment);
        }
    }

    @Override
    public void getIntentExtras(Intent intent) {
        if (intent != null){
            webUrl = intent.getStringExtra("webUrl");
        }
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
    protected void doOnToolbarBackClick(View v) {
        super.doOnToolbarBackClick(v);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webViewFragment != null){
            boolean dealResult = webViewFragment.onKeyDownForWebViewFragment(keyCode, event);
            if (dealResult){
                return super.onKeyDown(keyCode, event);
            }else {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected boolean isNeedToolbar() {
        return false;
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
