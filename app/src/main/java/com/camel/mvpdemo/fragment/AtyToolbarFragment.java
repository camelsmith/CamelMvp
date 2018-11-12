package com.camel.mvpdemo.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.camel.mvp.base.MvpBasePresenter;
import com.camel.mvpdemo.R;
import com.camel.mvpdemo.fragment.base.AppBaseFragment;
import com.camel.mvpdemo.fragment.base.AppBaseToolbarFragment;
import com.camel.mvpdemo.presenter.EmptyPresenter;
import com.camel.mvpdemo.viewinterface.EmptyInterface;

/**
 * Created by CamelLuo on 2018/10/25.
 */
public class AtyToolbarFragment extends AppBaseToolbarFragment<EmptyInterface, EmptyPresenter> implements EmptyInterface {

    @Override
    protected void getBundleValues(Bundle bundle) {

    }

    @Override
    protected int setContentViewId() {
        return R.layout.fragment_include_toolbar_aty;
    }

    @Override
    protected void initViewAndToolbar(View view, Bundle savedInstanceState) {
        initToolBar(true,"附带ToolbarFragment的Activity");
    }

    @Override
    protected MvpBasePresenter createPresenter() {
        return new EmptyPresenter();
    }

    /**
     * 实例化Fragment的入口，可以携带参数传入
     * @return
     */
    public static AtyToolbarFragment newInstance() {
        AtyToolbarFragment fragment = new AtyToolbarFragment();
        return fragment;
    }

    @Override
    protected void doOnToolbarBackClick(View v) {
        super.doOnToolbarBackClick(v);
        mActivity.finish();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }


    @Override
    protected boolean isNeedToolbar() {
        return true;
    }

    @Override
    protected boolean isNeedStatusBarFontDark() {
        return false;
    }
}
