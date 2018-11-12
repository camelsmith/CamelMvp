package com.camel.mvpdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.camel.mvpdemo.R;
import com.camel.mvpdemo.activity.base.AppBaseActivity;
import com.camel.mvpdemo.fragment.AtyFragment;
import com.camel.mvpdemo.fragment.AtyToolbarFragment;
import com.camel.mvpdemo.presenter.EmptyPresenter;
import com.camel.mvpdemo.viewinterface.EmptyInterface;

/**
 * Created by CamelLuo on 2018/10/25.
 */
public class ToolbarFragmentActivity extends AppBaseActivity<EmptyInterface, EmptyPresenter> implements EmptyInterface {
    @Override
    protected int setContentViewId() {
        return R.layout.activity_toolbar_fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //初始化Fragment管理器
        initBaseFragmentManager(getSupportFragmentManager(),R.id.activity_toolbar_fragment_content_layout,null);
        //加载Fragment
        if (getBaseFragmentManager() != null){
            AtyToolbarFragment atyToolbarFragment = AtyToolbarFragment.newInstance();
            getBaseFragmentManager().add(atyToolbarFragment);
        }
    }

    @Override
    public void getIntentExtras(Intent intent) {

    }

    @Override
    protected void doOnToolbarBackClick(View v) {
        super.doOnToolbarBackClick(v);
        finish();
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
